package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.IterationSkippedException
import io.kotest.engine.TestAbortedException
import io.kotest.engine.test.TestResult

internal object HandleSkippedExceptionsTestInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return test(testCase, scope).let { testResult ->
         when (val error = testResult.errorOrNull) {
            is TestAbortedException -> TestResult.Ignored(error.reason)
            is IterationSkippedException -> TestResult.Ignored(error.reason)
            else -> testResult
         }
      }
   }
}

