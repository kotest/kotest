package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

/**
 * Checks that the user has not tried to use an invalid invocation count.
 */
object InvocationCountCheckTestExecutionInterceptor : TestExecutionInterceptor {
   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      if (testCase.config.invocations > 1 && testCase.type == TestType.Container)
         error("Cannot execute multiple invocations in parent tests")
      else
         test(testCase, context)
   }
}
