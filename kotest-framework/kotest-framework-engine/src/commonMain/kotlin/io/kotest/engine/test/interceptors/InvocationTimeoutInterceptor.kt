package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeoutOrNull
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
            testCase.config.timeout?.inWholeMilliseconds ?: 10000000,
            testCase.config.invocationTimeout?.inWholeMilliseconds ?: 10000000
         )

         logger.log { Pair(testCase.name.testName, "Switching context to add invocationTimeout $timeout") }

         return try {
            // we use orNull because we want to disambiguate between our timeouts and user level timeouts
            // user level timeouts will throw an exception, ours will return null
            withTimeoutOrNull(timeout) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            } ?: throw TestTimeoutException(timeout.milliseconds, testCase.name.testName)
         } catch (t: TimeoutCancellationException) {
            logger.log { Pair(testCase.name.testName, "Caught user timeout $t") }
            throw t
         }
      }
   }
}
