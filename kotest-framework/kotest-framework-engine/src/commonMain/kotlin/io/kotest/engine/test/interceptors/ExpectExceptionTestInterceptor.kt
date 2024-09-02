package io.kotest.engine.test.interceptors

import io.kotest.core.test.ExpectFailureException
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope

/**
 * A [TestExecutionInterceptor] that detects thrown [io.kotest.core.test.ExpectFailureException]
 * and adjusts tests from error to ignored.
 */
internal object ExpectExceptionTestInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      val result = test(testCase, scope)
      return when (result) {
         is TestResult.Error -> if (result.cause is ExpectFailureException) TestResult.Ignored("Ignored due to failed expectation") else result
         else -> result
      }
   }
}

