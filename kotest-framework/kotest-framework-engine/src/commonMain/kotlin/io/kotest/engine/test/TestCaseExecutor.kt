package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.extensions.AssertionModeTestExecutionExtension
import io.kotest.engine.test.extensions.CoroutineDebugProbeTestExecutionExtension
import io.kotest.engine.test.extensions.CoroutineScopeTestExecutionExtension
import io.kotest.engine.test.extensions.EnabledCheckTestExecutionExtension
import io.kotest.engine.test.extensions.ExceptionCapturingTestExecutionExtension
import io.kotest.engine.test.extensions.GlobalSoftAssertTestExecutionExtension
import io.kotest.engine.test.extensions.InvocationCountCheckTestExecutionExtension
import io.kotest.engine.test.extensions.InvocationTimeoutTestExecutionExtension
import io.kotest.engine.test.extensions.LifecycleTestExecutionExtension
import io.kotest.engine.test.extensions.SupervisorScopeTestExecutionExtension
import io.kotest.engine.test.extensions.TestCaseInterceptionTestExecutionExtension
import io.kotest.engine.test.extensions.TimeoutTestExecutionExtension
import io.kotest.mpp.log
import io.kotest.mpp.timeInMillis

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test.
 *
 */
class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val executionContext: TimeoutExecutionContext,
) {

   suspend fun execute(testCase: TestCase, context: TestContext): TestResult {
      log { "TestCaseExecutor: execute entry point [testCase=${testCase.displayName}, context=$context]" }

      val start = timeInMillis()

      val extensions = listOf(
         SupervisorScopeTestExecutionExtension,
         CoroutineDebugProbeTestExecutionExtension,
         EnabledCheckTestExecutionExtension,
         CoroutineScopeTestExecutionExtension,
         TestCaseInterceptionTestExecutionExtension,
         LifecycleTestExecutionExtension(listener, start),
         ExceptionCapturingTestExecutionExtension(start),
         InvocationCountCheckTestExecutionExtension,
         AssertionModeTestExecutionExtension(start),
         GlobalSoftAssertTestExecutionExtension,
         TimeoutTestExecutionExtension(executionContext),
         InvocationTimeoutTestExecutionExtension(executionContext),
      )

      val innerExecute: suspend (TestContext) -> TestResult = { ctx ->
         testCase.test(ctx)
         createTestResult(timeInMillis() - start, null)
      }

      val result = extensions.foldRight(innerExecute) { ext, fn ->
         if (ext.shouldApply(testCase)) ext.execute(testCase, fn) else fn
      }.invoke(context)

      when (result.status) {
         TestStatus.Ignored -> listener.testIgnored(testCase)
         else -> listener.testFinished(testCase, result)
      }

      return result
   }
}
