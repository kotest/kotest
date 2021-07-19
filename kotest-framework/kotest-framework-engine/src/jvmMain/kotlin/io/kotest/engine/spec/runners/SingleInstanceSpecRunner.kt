package io.kotest.engine.spec.runners

import io.kotest.core.config.configuration
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.concurrency.resolvedThreads
import io.kotest.engine.launchers.TestLauncher
import io.kotest.engine.lifecycle.invokeAfterSpec
import io.kotest.engine.lifecycle.invokeBeforeSpec
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.attach
import io.kotest.engine.test.names.DuplicateTestNameHandler
import io.kotest.engine.test.toTestResult
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
   private val executionContext: ExecutionContext,
   listener: TestEngineListener,
   launcher: TestLauncher,
) : SpecRunner(listener, launcher) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> {
      log { "SingleInstanceSpecRunner: executing spec [$spec]" }

      suspend fun interceptAndRun(context: CoroutineContext) = Try {
         val rootTests = spec.materializeAndOrderRootTests(executionContext).map { it.testCase }
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
      override val executionContext: ExecutionContext,
   ) : TestContext {

      private val handler = DuplicateTestNameHandler(configuration.duplicateTestNameMode)
      private var failedfast = false

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         log { "Nested test case discovered $nested" }

         val t = nested.attach(testCase, handler.handle(nested.name), executionContext)

         if (failedfast) {
            log { "A previous nested test failed and failfast is enabled - will mark this as ignored" }
            listener.testIgnored(t, "Failfast enabled on parent test")
         } else {
            // if running this nested test results in an error, we won't launch any more nested tests
            val result = runTest(t, coroutineContext)
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
         override fun testStarted(testCase: TestCase) {
            listener.testStarted(testCase)
         }

         override fun testIgnored(testCase: TestCase) {
            listener.testIgnored(testCase, null)
         }

         override fun testFinished(testCase: TestCase, result: TestResult) {
            listener.testFinished(testCase, result)
         }
      }, ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) })

      val result = testExecutor.execute(testCase, Context(testCase, coroutineContext, executionContext))
      results[testCase] = result
      return result
   }
}
