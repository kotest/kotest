package io.kotest.engine.test.interceptors

import io.kotest.common.TimeMarkCompat
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

/**
 * A [TestExecutionInterceptor] that installs a general timeout for all invocations of a test.
 */
internal class TimeoutInterceptor(
   private val mark: TimeMarkCompat,
) : TestExecutionInterceptor {

   private val logger = Logger(TimeoutInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      // this timeout applies to the test itself. If the test has multiple invocations then
      // this timeout applies across all invocations. In other words, if a test has invocations = 3,
      // each test takes 300ms, and a timeout of 800ms, this would fail, becauase 3 x 300 > 800.
      logger.log { Pair(testCase.name.testName, "Switching context to add timeout ${testCase.config.timeout}") }

      return when (val timeout = testCase.config.timeout) {
         null -> test(testCase, scope)
         else -> try {
            withTimeout(timeout) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         } catch (t: Throwable) {
            logger.log { Pair(testCase.name.testName, "Caught timeout $t") }
            TestResult.Error(mark.elapsedNow(), TestTimeoutException(timeout, testCase.name.testName))
         }
      }
   }
}

/**
 * Exception used for when a test exceeds its timeout.
 */
open class TestTimeoutException(val timeout: Duration, val testName: String) :
   Exception("Test '${testName}' did not complete within $timeout")
