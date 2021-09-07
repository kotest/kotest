package io.kotest.engine.test

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDispatcherTestExecutionInterceptor
import io.kotest.engine.test.interceptors.CoroutineScopeInterceptor
import io.kotest.engine.test.interceptors.EnabledCheckInterceptor
import io.kotest.engine.test.interceptors.ExceptionCapturingInterceptor
import io.kotest.engine.test.interceptors.GlobalSoftAssertInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
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
   private val executionContext: InterruptableExecutionContext,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point [testCase=${testCase.displayName}, context=$context]" }

      val start = timeInMillis()

      val interceptors = listOf(
         InvocationCountCheckInterceptor,
         CoroutineDebugProbeInterceptor,
         SupervisorScopeInterceptor,
         CoroutineDispatcherTestExecutionInterceptor(configuration),
         TestCaseExtensionInterceptor,
         EnabledCheckInterceptor,
         LifecycleInterceptor(listener, start),
         ExceptionCapturingInterceptor(start),
         AssertionModeInterceptor,
         GlobalSoftAssertInterceptor,
         TimeoutInterceptor(executionContext, start),
         // this MUST BE AFTER the timeout interceptor, as any cancellation there must cancel
         // user launched coroutines (children of this scope)
         CoroutineScopeInterceptor,
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
