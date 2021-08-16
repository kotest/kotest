package io.kotest.engine.test.extensions

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.TestTimeoutException
import io.kotest.engine.test.TimeoutExecutionContext
import io.kotest.mpp.log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * The [TimeoutExecutionContext] is used to provide a way of executing functions on the underlying platform
 * in a way that best utilizes threads or the lack of on that platform.
 */
class TimeoutTestExecutionExtension(
   private val ec: TimeoutExecutionContext,
) : TestExecutionExtension {

   private fun resolvedTimeout(testCase: TestCase): Long =
      testCase.config.timeout?.inWholeMilliseconds
         ?: testCase.spec.timeout
         ?: testCase.spec.timeout()
         ?: configuration.timeout

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult {

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      val timeout = resolvedTimeout(testCase)
      log { "TestCaseExecutor: Test [${testCase.displayName}] will execute with timeout $timeout" }

      return { context ->
         try {
            withTimeout(timeout) {
               ec.executeWithTimeoutInterruption(timeout) { test(context) }
            }
         } catch (e: TimeoutCancellationException) {
            when (testCase.type) {
               TestType.Container -> throw TestTimeoutException(timeout, testCase.displayName)
               TestType.Test -> throw TestTimeoutException(timeout, testCase.displayName)
            }
         }
      }
   }
}
