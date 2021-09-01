package io.kotest.engine.test

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.extensions.AssertionModeTestExecutionFilter
import io.kotest.engine.test.extensions.CoroutineDebugProbeTestExecutionFilter
import io.kotest.engine.test.extensions.CoroutineDispatcherTestExecutionFilter
import io.kotest.engine.test.extensions.CoroutineScopeTestExecutionFilter
import io.kotest.engine.test.extensions.EnabledCheckTestExecutionFilter
import io.kotest.engine.test.extensions.ExceptionCapturingTestExecutionFilter
import io.kotest.engine.test.extensions.GlobalSoftAssertTestExecutionFilter
import io.kotest.engine.test.extensions.InvocationCountCheckTestExecutionFilter
import io.kotest.engine.test.extensions.LifecycleTestExecutionFilter
import io.kotest.engine.test.extensions.SupervisorScopeTestExecutionFilter
import io.kotest.engine.test.extensions.TestCaseInterceptionTestExecutionFilter
import io.kotest.engine.test.extensions.TimeoutTestExecutionFilter
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val executionContext: TimeoutExecutionContext,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point [testCase=${testCase.displayName}, context=$context]" }

      val start = timeInMillis()

      val pipeline = listOf(
         CoroutineDebugProbeTestExecutionFilter,
      //   CoroutineDispatcherTestExecutionFilter(configuration),
         TestCaseInterceptionTestExecutionFilter,
         EnabledCheckTestExecutionFilter,
         LifecycleTestExecutionFilter(listener, start),
         ExceptionCapturingTestExecutionFilter(start),
         InvocationCountCheckTestExecutionFilter,
         SupervisorScopeTestExecutionFilter,
         TimeoutTestExecutionFilter(executionContext, start),
         AssertionModeTestExecutionFilter,
         GlobalSoftAssertTestExecutionFilter,
         CoroutineScopeTestExecutionFilter,
      )

      val innerExecute: suspend (TestCase, TestContext) -> TestResult = { tc, ctx ->
         tc.test(ctx)
         createTestResult(timeInMillis() - start, null)
      }

      val result = pipeline.foldRight(innerExecute) { ext, fn ->
         { tc, ctx -> ext.execute(fn)(tc, ctx) }
      }.invoke(testCase, context)

      when (result.status) {
         TestStatus.Ignored -> listener.testIgnored(testCase)
         else -> listener.testFinished(testCase, result)
      }

      return result
   }
}
