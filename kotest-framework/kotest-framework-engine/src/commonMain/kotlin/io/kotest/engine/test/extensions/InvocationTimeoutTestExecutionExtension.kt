package io.kotest.engine.test.extensions

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.TestTimeoutException
import io.kotest.engine.events.invokeAfterInvocation
import io.kotest.engine.events.invokeBeforeInvocation
import io.kotest.engine.test.TimeoutExecutionContext
import io.kotest.mpp.log
import io.kotest.mpp.replay
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.min

class InvocationTimeoutTestExecutionExtension(
   private val ec: TimeoutExecutionContext,
) : TestExecutionExtension {

   private fun resolvedTimeout(testCase: TestCase): Long =
      testCase.config.timeout?.inWholeMilliseconds
         ?: testCase.spec.timeout
         ?: testCase.spec.timeout()
         ?: configuration.timeout

   /**
    * Returns the resolved timeout for a test invocation taking into account config on the test case,
    * values specified in the spec itself, and project wide defaults.
    */
   private fun resolvedInvocationTimeout(testCase: TestCase): Long =
      testCase.config.invocationTimeout?.inWholeMilliseconds
         ?: testCase.spec.invocationTimeout()
         ?: testCase.spec.invocationTimeout
         ?: configuration.invocationTimeout

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      // this timeout applies to each invocation. If a test has invocations = 3, and this timeout
      // is set to 300ms, then each individual invocation must complete in under 300ms.
      // invocation timeouts are not applied to TestType.Container only TestType.Test

      // note: the invocation timeout cannot be larger than the test case timeout
      val invocationTimeout = min(resolvedTimeout(testCase), resolvedInvocationTimeout(testCase))
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with invocationTimeout $invocationTimeout" }

      // depending on the test type, we execute with an invocation timeout
      try {
         when (testCase.type) {
            TestType.Container -> test(context)
            TestType.Test -> {
               var result = TestResult.success(0)
               // not all platforms support executing with an interruption based timeout
               // because it uses background threads to interrupt
               replay(
                  testCase.config.invocations,
                  testCase.config.threads,
                  { testCase.invokeBeforeInvocation(it) },
                  { testCase.invokeAfterInvocation(it) }) {
                  ec.executeWithTimeoutInterruption(invocationTimeout) {
                     withTimeout(invocationTimeout) {
                        result = test(context)
                     }
                  }
               }
               result
            }
         }
      } catch (e: TimeoutCancellationException) {
         throw TestTimeoutException(invocationTimeout, testCase.displayName)
      }
   }
}
