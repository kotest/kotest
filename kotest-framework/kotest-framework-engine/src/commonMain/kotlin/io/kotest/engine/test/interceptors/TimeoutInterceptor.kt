package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.resolvedTimeout
import io.kotest.engine.test.contexts.withCoroutineContext
import io.kotest.mpp.log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * A [TestExecutionInterceptor] that installs a general timeout for all invocations of a test.
 */
internal object TimeoutInterceptor : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      val timeout = resolvedTimeout(testCase)
      log { "TimeoutInterceptor: Test [${testCase.displayName}] will execute with timeout ${timeout}ms" }

      try {
         log { "TimeoutInterceptor: Switching context to add timeout $timeout" }
         withTimeout(timeout) {
            test(testCase, context.withCoroutineContext(coroutineContext))
         }
      } catch (e: TimeoutCancellationException) {
         log { "TimeoutInterceptor: Caught TimeoutCancellationException ${e.message}" }
         throw TestTimeoutException(timeout, testCase.displayName)
      }
   }
}

/**
 * Exception used for when a test exceeds its timeout.
 */
class TestTimeoutException(val timeout: Long, val testName: String) :
   Exception("Test '$testName' did not complete within ${timeout}ms")
