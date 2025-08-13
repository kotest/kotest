package io.kotest.engine.spec.execution

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Implementation of [SpecExecutor] that executes each [TestCase] in a fresh instance
 * of the [Spec] class.
 *
 * This differs from the [InstancePerLeafSpecExecutor] in that
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
@Suppress("DEPRECATION")
@Deprecated("The semantics of instance per leaf are confusing and this mode should be avoided")
internal class InstancePerTestSpecExecutor(
   private val context: EngineContext,
) : SpecExecutor {

   private val logger = Logger(InstancePerTestSpecExecutor::class)
   private val materializer = Materializer(context.specConfigResolver)
   private val pipeline = SpecInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val results = TestResults()

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   /**
    * The intention of this executor is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case,
    * we must first instantiate a new spec, and begin execution on _that_ instance.
    *
    * As test lambdas are executed, nested test cases will be registered, these should be ignored
    * if they are not an ancestor of the target. If they are then we can step into them, and
    * continue recursively until we find the target.
    *
    * Once the target is found it can be executed as normal, and any test lambdas it contains
    * can be launched for execution.
    */
   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {

         // for the seed spec that is passed in, we need to run the instance pipeline,
         // then register all the root tests. These root tests will either execute in the
         // seed instance (for the first test), or in a fresh instance (for the rest).

         val specContext = SpecContext.create()
         pipeline.execute(seed, specContext) {
            launchRootTests(seed, ref, specContext)
            Result.success(results.toMap())
         }.map { results.toMap() }
      }
   }

   private suspend fun launchRootTests(seed: Spec, ref: SpecRef, specContext: SpecContext) {

      val rootTests = materializer.materialize(seed)

      // controls how many tests to execute concurrently
      val concurrency = context.specConfigResolver.testExecutionMode(seed).concurrency
      val semaphore = Semaphore(concurrency)

      // all root test coroutines are launched immediately,
      // the semaphore will control how many can actually run concurrently

      coroutineScope { // will wait for all tests to complete
         rootTests.withIndex().toList().forEach { (index, root) ->
            launch {
               semaphore.withPermit {
                  if (index == 0) {
                     /**
                      * The first time we run a root test, we can use the already instantiated spec as the instance.
                      * This avoids creating specs that do nothing other than scheduling tests for other specs to run in.
                      * Eg, see https://github.com/kotest/kotest/issues/3490
                      */
                     executeTest(root, root.descriptor, specContext, ref)
                  } else {
                     // for subsequent tests, we create a new instance of the spec
                     // and will re-run the pipelines etc
                     executeInFreshSpec(root, ref, specContext)
                  }
               }
            }
         }
      }
   }

   /**
    * Executes the given [TestCase] in a fresh spec instance.
    *
    * It will create a new spec instance and run the pipeline on that, before
    * using that spec for the test execution.
    *
    * It will locate the root that is the parent of the given [TestCase] and execute it in the new spec instance.
    */
   private suspend fun executeInFreshSpec(testCase: TestCase, ref: SpecRef, specContext: SpecContext) {
      logger.log { "Starting test ${testCase.descriptor}" }

      val spec = inflator.inflate(ref).getOrThrow()

      // we need to find the same root test but in the newly created spec
      val root = materializer.materialize(spec).first { it.descriptor.isPrefixOf(testCase.descriptor) }
      logger.log { "Located root for target $root" }

      // we switch to a new coroutine for each spec instance
      withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         pipeline.execute(spec, specContext) {
            val result = executeTest(root, testCase.descriptor, specContext, ref)
            Result.success(mapOf(testCase to result))
         }
      }
   }

   /**
    * Executes the given [TestCase] using a [TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private suspend fun executeTest(
      testCase: TestCase,
      target: Descriptor.TestDescriptor,
      specContext: SpecContext,
      ref: SpecRef,
   ): TestResult {
      val executor = TestCaseExecutor(
         // we need a special listener that only listens to the target test case events
         listener = TargetListeningListener(target, context.listener),
         context = context
      )
      val result = executor.execute(
         testCase = testCase,
         testScope = LaunchingTestScope(
            testCase = testCase,
            target = target,
            specContext = specContext,
            ref = ref,
            coroutineContext = coroutineContext,
         ),
         specContext = specContext
      )
      results.completed(testCase, result)
      return result
   }

   /**
    * A [TestScope] that launches nested tests for execution in their own spec instance.
    */
   inner class LaunchingTestScope(
      override val testCase: TestCase,
      private val target: Descriptor.TestDescriptor,
      private val specContext: SpecContext,
      private val ref: SpecRef,
      override val coroutineContext: CoroutineContext,
   ) : TestScope {

      private val logger = Logger(LaunchingTestScope::class)

      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.name, "Discovered nested test '${nested}'") }

         val nestedTestCase = materializer.materialize(nested, testCase)

         // we only care about nested tests in two scenarios:
         // - if the current test is the target, then any nested tests are new and should be executed
         // - if the discovered test is on the path to the target, or is the target, then we can continue executing in the same spec
         // otherwise we ignore it
         if (target == testCase.descriptor) {
            logger.log { Pair(testCase.name.name, "Launching discovered test as first discovery") }
            executeInFreshSpec(nestedTestCase, ref, specContext)
         } else if (nestedTestCase.descriptor.isPrefixOf(target)) {
            logger.log { Pair(testCase.name.name, "Launching discovered test as parent of target $target") }
            executeTest(nestedTestCase, target, specContext, ref)
         }
      }
   }

   internal class TargetListeningListener(
      private val target: Descriptor.TestDescriptor,
      private val delegate: TestEngineListener,
   ) : TestCaseExecutionListener {

      override suspend fun testStarted(testCase: TestCase) {
         if (target == testCase.descriptor) delegate.testStarted(testCase)
      }

      override suspend fun testIgnored(testCase: TestCase, reason: String?) {
         if (target == testCase.descriptor) delegate.testIgnored(testCase, reason)
      }

      override suspend fun testFinished(testCase: TestCase, result: TestResult) {
         if (target == testCase.descriptor) delegate.testFinished(testCase, result)
      }
   }
}
