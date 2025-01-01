package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.Logger
import kotlin.time.Duration

/**
 * Checks that the user has not tried to use an invalid invocation count.
 */
internal object InvocationCountCheckInterceptor : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      logger.log { Pair(testCase.name.name, "Checking that invocation count is 1 for containers") }
      return when {
         testCase.type == TestType.Test || testCase.config.invocations <= 1 -> test(testCase, scope)
         else -> TestResult.Error(
            Duration.ZERO,
            Exception("Cannot execute multiple invocations in parent tests")
         )
      }
   }
}
