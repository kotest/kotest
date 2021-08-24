package io.kotest.engine.spec.runners

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.createTestName
import io.kotest.core.test.toTestCase
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.concurrency.resolvedThreads
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.engine.events.invokeAfterSpec
import io.kotest.engine.events.invokeBeforeSpec
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.DuplicateTestNameHandler
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * Implementation of [SpecRunner] that executes all tests against the
 * same [Spec] instance. In other words, only a single instance of the spec class
 * is instantiated for all the test cases.
 */
internal class SingleInstanceSpecRunner(
   listener: TestEngineListener,
   scheduler: TestScheduler,
) : SpecRunner(listener, scheduler) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {
      log { "SingleInstanceSpecRunner: executing spec [$spec]" }

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         val rootTests = spec.materializeAndOrderRootTests().map { it.testCase }
         log { "SingleInstanceSpecRunner: Materialized root tests: ${rootTests.size}" }
         val threads = spec.resolvedThreads()
         if (threads != null && threads > 1) {
            log { "Warning - usage of deprecated thread count $threads" }
            runParallel(threads, rootTests) {
               log { "SingleInstanceSpecRunner: Executing test $it" }
               runTest(it, context)
            }
         } else {
            launch(spec) {
               log { "SingleInstanceSpecRunner: Executing test $it" }
               runTest(it, context)
            }
         }
      }

      return coroutineScope {
         spec.invokeBeforeSpec()
            .flatMap { interceptAndRun(coroutineContext) }
            .flatMap { spec.invokeAfterSpec() }
            .map { results }
      }
   }

   inner class Context(
      override val testCase: TestCase,
      override val coroutineContext: CoroutineContext,
   ) : TestContext {

      private val handler = DuplicateTestNameHandler(configuration.duplicateTestNameMode)
      private var failedfast = false

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         log { "Nested test case discovered $nested" }
         val overrideName = handler.handle(nested.name)?.let { createTestName(it) }
         val nestedTestCase = nested.toTestCase(testCase.spec, testCase, overrideName)
         if (failedfast) {
            log { "A previous nested test failed and failfast is enabled - will mark this as ignored" }
            listener.testIgnored(nestedTestCase, "Failfast enabled on parent test")
         } else {
            // if running this nested test results in an error, we won't launch any more nested tests
            val result = runTest(nestedTestCase, coroutineContext)
            if (testCase.config.failfast == true) {
               if (result.status == TestStatus.Failure || result.status == TestStatus.Error) {
                  failedfast = true
               }
            }
         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      coroutineContext: CoroutineContext,
   ): TestResult {
      val testExecutor = TestCaseExecutor(object : TestCaseExecutionListener {
         override suspend fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase)
         }

         override suspend fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase, null)
         }

         override suspend fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase, result)
         }
      }, ExecutorExecutionContext)

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext))
      results[testCase] = result
      return result
   }
}
