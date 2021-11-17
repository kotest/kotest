package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

/**
 * Installs an invocation timeout.
 */
internal object InvocationTimeoutInterceptor : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      return if (testCase.type == TestType.Container) {
         test(testCase, scope)
      } else {

         // note: the invocation timeout cannot be larger than the test case timeout
         val timeout = min(
            testCase.config.timeout.inWholeMilliseconds,
            testCase.config.invocationTimeout.inWholeMilliseconds
         ).milliseconds

         logger.log { Pair(testCase.name.testName, "Switching context to add invocationTimeout $timeout") }

         return try {
            withTimeout(timeout) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         } catch (t: TimeoutCancellationException) {
            logger.log { Pair(testCase.name.testName, "Caught invocation timeout $t") }
            throw TestTimeoutException(timeout, testCase.name.testName)
         }
      }
   }
}
