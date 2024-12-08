package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.TestExtensions
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.mpp.bestName
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, the provided instance of the spec class
 * is used for all test cases.
 */
@ExperimentalKotest
@OptIn(KotestInternal::class)
internal class SingleInstanceSpecRunner(
   private val scheduler: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecRunner {

   private val results = ConcurrentHashMap<TestCase, TestResult>()
   private val logger = Logger(SingleInstanceSpecRunner::class)
   private val pipeline = SpecInterceptorPipeline(context)
   private val materializer = Materializer(context.configuration)
   private val listener = context.listener
   private val specContext = SpecContext.create()

   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "executing spec $spec") }
      try {
         return coroutineScope {
            pipeline.execute(spec, object : NextSpecInterceptor {
               override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
                  val rootTests = materializer.materialize(spec)
                  logger.log { Pair(spec::class.bestName(), "Launching ${rootTests.size} root tests on $scheduler") }
                  scheduler.schedule({ runTest(it, coroutineContext, null) }, rootTests)
                  return Result.success(results)
               }
            })
         }
      } catch (e: Exception) {
         e.printStackTrace()
         throw e
      }
   }

   /**
    * A [TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via TestCase config or globally.
    */
   inner class SingleInstanceTestScope(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
      private val parentScope: SingleInstanceTestScope?,
   ) : TestScope {

      // set to true if we failed fast and should ignore further tests
      private var skipRemaining = false

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.testName, "Registering nested test '${nested}") }

         val nestedTestCase = Materializer(context.configuration).materialize(nested, testCase)
         if (skipRemaining) {
            val reason = "Skipping test due to fail fast"
            logger.log { Pair(testCase.name.testName, reason) }
            listener.testIgnored(nestedTestCase, reason)
            TestExtensions(context.configuration.registry).ignoredTestListenersInvocation(nestedTestCase, reason)
         } else {
            // if running this nested test results in an error, we won't launch anymore nested tests
            val result = runTest(nestedTestCase, coroutineContext, this@SingleInstanceTestScope)
            if (result.isErrorOrFailure) {
               if (testCase.config.failfast || context.configuration.projectWideFailFast) {
                  logger.log { Pair(testCase.name.testName, "Test failed - setting skipRemaining = true") }
                  skipRemaining = true
                  parentScope?.skipRemaining = true
               }
            }
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
      parentScope: SingleInstanceTestScope?,
   ): TestResult {

      val testExecutor = TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
         defaultCoroutineDispatcherFactory,
         context,
      )

      val scope = DuplicateNameHandlingTestScope(
         context.configuration.duplicateTestNameMode,
         SingleInstanceTestScope(testCase, coroutineContext, parentScope)
      )

      val result = testExecutor.execute(testCase, scope, specContext)
      results[testCase] = result
      return result
   }
}
