package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.mpp.log

/**
 * Checks that the user has not tried to use an invalid invocation count.
 */
internal object InvocationCountCheckInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      log { "InvocationCountCheckInterceptor: Checking that invocation count is 1 for containers" }
      if (testCase.config.invocations > 1 && testCase.type == TestType.Container)
         error("Cannot execute multiple invocations in parent tests")
      else
         test(testCase, context).apply {
            log { "rrrrrrrr" }
         }
   }
}
