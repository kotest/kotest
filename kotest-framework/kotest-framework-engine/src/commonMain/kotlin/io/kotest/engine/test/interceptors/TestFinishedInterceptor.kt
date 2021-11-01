package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestCaseExecutionListener

internal class TestFinishedInterceptor(private val listener: TestCaseExecutionListener) : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val result = test(testCase, scope)
      when (result) {
         is TestResult.Ignored -> listener.testIgnored(testCase, result.reason)
         else -> listener.testFinished(testCase, result)
      }
      return result
   }
}
