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
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.min

/**
 * The [TimeoutExecutionContext] is used to provide a way of executing functions on the underlying platform
 * in a way that best utilizes threads or the lack of on that platform.
 */
class TimeoutTestExecutionFilter(
   private val ec: TimeoutExecutionContext,
   private val start: Long,
) : TestExecutionFilter {

   private fun resolvedTimeout(testCase: TestCase): Long =
      testCase.config.timeout?.inWholeMilliseconds
         ?: testCase.spec.timeout
         ?: testCase.spec.timeout()
         ?: configuration.timeout

   private fun resolvedInvocationTimeout(testCase: TestCase): Long =
      testCase.config.invocationTimeout?.inWholeMilliseconds
         ?: testCase.spec.invocationTimeout()
         ?: testCase.spec.invocationTimeout
         ?: configuration.invocationTimeout

   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      val timeout = resolvedTimeout(testCase)
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with timeout $timeout" }

      // note: the invocation timeout cannot be larger than the test case timeout
      val invocationTimeout = min(resolvedTimeout(testCase), resolvedInvocationTimeout(testCase))
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with invocationTimeout $invocationTimeout" }

      try {
         withTimeout(timeout) {
            ec.executeWithTimeoutInterruption(timeout) {
               // depending on the test type, we execute with an invocation timeout
               when (testCase.type) {
                  TestType.Container -> test(testCase, context)
                  TestType.Test -> {
                     // not all platforms support executing with an interruption based timeout
                     // because it uses background threads to interrupt
                     replay(
                        testCase.config.invocations,
                        testCase.config.threads,
                        { testCase.invokeBeforeInvocation(it) },
                        { testCase.invokeAfterInvocation(it) }) {
                        ec.executeWithTimeoutInterruption(invocationTimeout) {
                           withTimeout(invocationTimeout) {
                              test(testCase, context)
                           }
                        }
                     }
                     TestResult.success(timeInMillis() - start)
                  }
               }
            }
         }
      } catch (e: TimeoutCancellationException) {
         when (testCase.type) {
            TestType.Container -> throw TestTimeoutException(timeout, testCase.displayName)
            TestType.Test -> throw TestTimeoutException(min(timeout, invocationTimeout), testCase.displayName)
         }
      }
   }
}
