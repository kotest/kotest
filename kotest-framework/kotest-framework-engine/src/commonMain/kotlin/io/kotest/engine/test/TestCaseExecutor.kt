package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.interceptors.AssertionModeTestExecutionInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeTestExecutionInterceptor
import io.kotest.engine.test.interceptors.CoroutineScopeTestExecutionInterceptor
import io.kotest.engine.test.interceptors.EnabledCheckTestExecutionInterceptor
import io.kotest.engine.test.interceptors.ExceptionCapturingTestExecutionInterceptor
import io.kotest.engine.test.interceptors.GlobalSoftAssertTestExecutionInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckTestExecutionInterceptor
import io.kotest.engine.test.interceptors.LifecycleTestExecutionInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TestCaseInterceptionTestExecutionInterceptor
import io.kotest.engine.test.interceptors.TimeoutTestExecutionInterceptor
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

      val interceptors = listOf(
         InvocationCountCheckTestExecutionInterceptor,
         CoroutineDebugProbeTestExecutionInterceptor,
         SupervisorScopeTestExecutionInterceptor,
         CoroutineScopeTestExecutionInterceptor,
//         CoroutineDispatcherTestExecutionFilter(configuration),
         TestCaseInterceptionTestExecutionInterceptor,
         EnabledCheckTestExecutionInterceptor,
         LifecycleTestExecutionInterceptor(listener, start),
         ExceptionCapturingTestExecutionInterceptor(start),
         AssertionModeTestExecutionInterceptor,
         GlobalSoftAssertTestExecutionInterceptor,
         TimeoutTestExecutionInterceptor(executionContext, start),
      )

      val innerExecute: suspend (TestCase, TestContext) -> TestResult = { tc, ctx ->
         tc.test(ctx)
         createTestResult(timeInMillis() - start, null)
      }

      val result = interceptors.foldRight(innerExecute) { ext, fn ->
         { tc, ctx -> ext.intercept(fn)(tc, ctx) }
      }.invoke(testCase, context)

      when (result.status) {
         TestStatus.Ignored -> listener.testIgnored(testCase)
         else -> listener.testFinished(testCase, result)
      }

      return result
   }
}
