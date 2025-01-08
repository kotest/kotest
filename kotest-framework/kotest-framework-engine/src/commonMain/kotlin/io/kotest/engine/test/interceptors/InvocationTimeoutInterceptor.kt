package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Installs an invocation timeout.
 */
internal class InvocationTimeoutInterceptor(private val testConfigResolver: TestConfigResolver) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      return if (testCase.type == TestType.Container) {
         test(testCase, scope)
      } else {

         val timeout = testConfigResolver.timeout(testCase)
         logger.log { Pair(testCase.name.name, "Switching context to add invocationTimeout $timeout") }

         try {
            // we use orNull because we want to disambiguate between our timeouts and user level timeouts
            // user level timeouts will throw an exception, ours will return null
            withTimeoutOrNull(timeout) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            } ?: throw TestTimeoutException(timeout, testCase.name.name)
         } catch (t: TimeoutCancellationException) {
            logger.log { Pair(testCase.name.name, "Caught user timeout $t") }
            throw t
         }
      }
   }
}
