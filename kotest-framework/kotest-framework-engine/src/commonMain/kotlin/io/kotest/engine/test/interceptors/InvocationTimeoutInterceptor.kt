package io.kotest.engine.test.interceptors

import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.test.contexts.withCoroutineContext
import io.kotest.engine.test.resolvedInvocationTimeout
import io.kotest.engine.test.resolvedTimeout
import io.kotest.mpp.log
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.min
import kotlin.time.milliseconds

/**
 * Installs an invocation timeout.
 */
internal class InvocationTimeoutInterceptor(
   private val configuration: Configuration,
) : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      if (testCase.type == TestType.Container) {
         test(testCase, context)
      } else {

         // note: the invocation timeout cannot be larger than the test case timeout
         val timeout = min(
            resolvedTimeout(testCase, configuration.timeout.milliseconds).inWholeMilliseconds,
            resolvedInvocationTimeout(testCase, configuration.invocationTimeout.milliseconds).inWholeMilliseconds
         ).milliseconds
         log { "InvocationTimeoutInterceptor: Test [${testCase.name.testName}] will execute with invocationTimeout ${timeout}ms" }

         log { "InvocationTimeoutInterceptor: Switching context to add invocationTimeout $timeout" }
         try {
            withTimeout(timeout) {
               test(testCase, context.withCoroutineContext(coroutineContext))
            }
         } catch (e: TimeoutCancellationException) {
            log { "InvocationTimeoutInterceptor: Caught TimeoutCancellationException ${e.message}" }
            throw TestTimeoutException(timeout, testCase.name.testName)
         }
      }
   }
}
