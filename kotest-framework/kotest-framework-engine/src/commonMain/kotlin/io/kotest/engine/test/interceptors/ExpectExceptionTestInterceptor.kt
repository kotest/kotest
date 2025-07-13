package io.kotest.engine.test.interceptors

import io.kotest.core.test.ExpectFailureException
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestResultBuilder

/**
 * A [TestExecutionInterceptor] that detects thrown [io.kotest.core.test.ExpectFailureException]
 * and adjusts tests from error to ignored.
 */
internal object ExpectExceptionTestInterceptor : TestExecutionInterceptor {

   private val IGNORED_RESULT = TestResultBuilder.builder()
      .withIgnoreReason("Ignored due to failed expectation")
      .build()

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      val result = test(testCase, scope)
      return when (result) {
         is TestResult.Error -> if (result.cause is ExpectFailureException) IGNORED_RESULT else result
         else -> result
      }
   }
}

