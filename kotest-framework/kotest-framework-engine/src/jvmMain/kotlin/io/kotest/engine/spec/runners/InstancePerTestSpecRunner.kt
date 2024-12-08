package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.flatMap
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.createAndInitializeSpec
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.core.Logger
import io.kotest.mpp.bestName
import io.kotest.core.log
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes each [TestCase] in a fresh instance
 * of the [Spec] class.
 *
 * This differs from the [InstancePerLeafSpecRunner] in that
 * every single test, whether of type [TestType.Test] or [TestType.Container], will be
 * executed separately. Branch tests will ultimately be executed once as a standalone
 * test, and also as part of the "path" to any nested tests.
 *
 * So, given the following structure:
 *
 * ```
 * outerTest {
 *   innerTestA {
 *     // test
 *   }
 *   innerTestB {
 *     // test
 *   }
 * }
 * ```
 *
 * Three spec instances will be created. The execution process will be:
 *
 * ```
 * spec1 = instantiate spec
 * spec1.outerTest
 * spec2 = instantiate spec
 * spec2.outerTest
 * spec2.innerTestA
 * spec3 = instantiate spec
 * spec3.outerTest
 * spec3.innerTestB
 * ```
 */
@ExperimentalKotest
internal class InstancePerTestSpecRunner(
   private val scheduler: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecRunner {

   private val logger = Logger(InstancePerTestSpecRunner::class)

   // set to true once the initially supplied spec has been used for a test
   private val defaultInstanceUsed = AtomicBoolean(false)

   private val materializer = Materializer(context.configuration)
   private val results = ConcurrentHashMap<TestCase, TestResult>()
   private val pipeline = SpecInterceptorPipeline(context)
   private val listener = context.listener

   /**
    * The intention of this runner is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      return runCatching {
         val rootTests = materializer.materialize(spec)
         logger.log { Pair(spec::class.bestName(), "Launching ${rootTests.size} root tests on $scheduler") }
         scheduler.schedule({ executeInCleanSpecIfRequired(it, spec).getOrThrow() }, rootTests)
         results
      }
   }

   /**
    * The intention of this runner is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be registered back with the stack for execution later.
    */
   private suspend fun executeInCleanSpec(test: TestCase): Result<Map<TestCase, TestResult>> {
      return createAndInitializeSpec(test.spec::class, context.configuration.registry)
         .flatMap { spec -> executeInGivenSpec(test, spec, SpecContext.create()) }
   }

   private suspend fun executeInGivenSpec(
      test: TestCase,
      spec: Spec,
      specContext: SpecContext,
   ): Result<Map<TestCase, TestResult>> {
      return pipeline.execute(spec, object : NextSpecInterceptor {
         override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
            return run(spec, specContext, test, results).map { results }
         }
      })
   }

   /**
    * The first time we run a root test, we can use the already instantiated spec as the instance.
    * This avoids creating specs that do nothing other than scheduling tests for other specs to run in.
    * Eg, see https://github.com/kotest/kotest/issues/3490
    */
   private suspend fun executeInCleanSpecIfRequired(
      test: TestCase,
      defaultSpec: Spec
   ): Result<Map<TestCase, TestResult>> {
      return if (defaultInstanceUsed.compareAndSet(false, true)) {
         Result.success(defaultSpec).flatMap { executeInGivenSpec(test, it, SpecContext.create()) }
      } else {
         executeInCleanSpec(test)
      }
   }

   private suspend fun run(
      spec: Spec,
      specContext: SpecContext,
      test: TestCase,
      results: ConcurrentHashMap<TestCase, TestResult>,
   ): Result<Unit> =
      runCatching {
         log { "Created new spec instance $spec" }
         // we need to find the same root test but in the newly created spec
         val root = materializer.materialize(spec).first { it.descriptor.isOnPath(test.descriptor) }
         log { "Starting root test ${root.descriptor} in search of ${test.descriptor}" }
         run(root, test, specContext, results)
      }

   private suspend fun run(
      test: TestCase,
      target: TestCase,
      specContext: SpecContext,
      results: ConcurrentHashMap<TestCase, TestResult>
   ) {
      val isTarget = test.descriptor == target.descriptor
      coroutineScope {
         val context = object : TestScope {

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               val t = Materializer(context.configuration).materialize(nested, testCase)

               // if we are currently executing the target, then any registered tests are new, and we
               // should begin execution of them in fresh specs
               // otherwise if the test is on the path we can continue in the same spec
               if (isTarget) {
                  executeInCleanSpec(t).getOrThrow()
               } else if (t.descriptor.isOnPath(target.descriptor)) {
                  run(t, target, specContext, results)
               }
            }
         }

         val context2 = DuplicateNameHandlingTestScope(
            this@InstancePerTestSpecRunner.context.configuration.duplicateTestNameMode,
            context
         )

         val testExecutor = TestCaseExecutor(
            object : TestCaseExecutionListener {
               override suspend fun testStarted(testCase: TestCase) {
                  if (isTarget) listener.testStarted(testCase)
               }

               override suspend fun testIgnored(testCase: TestCase, reason: String?) {
                  if (isTarget) listener.testIgnored(testCase, reason)
               }

               override suspend fun testFinished(testCase: TestCase, result: TestResult) {
                  if (isTarget) listener.testFinished(testCase, result)
               }
            },
            defaultCoroutineDispatcherFactory,
            this@InstancePerTestSpecRunner.context
         )

         val result = testExecutor.execute(test, context2, specContext)
         results[test] = result
      }
   }
}
