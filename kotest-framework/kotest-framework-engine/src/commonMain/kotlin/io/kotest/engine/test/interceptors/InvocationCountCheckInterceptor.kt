package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.TestResultBuilder

/**
 * Checks that the user has not tried to use an invalid invocation count.
 */
internal class InvocationCountCheckInterceptor(
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   companion object {
      private const val ERROR_MESSAGE = "Cannot execute multiple invocations in container tests"
   }

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      logger.log { Pair(testCase.name.name, "Checking that invocation count is 1 for containers") }
      return when {
         testCase.type == TestType.Container && testConfigResolver.invocations(testCase) > 1 ->
            TestResultBuilder.builder().withFailure(ERROR_MESSAGE).build()
         else -> test(testCase, scope)
      }
   }
}
