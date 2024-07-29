package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.core.Logger

internal class TestFinishedInterceptor(
   private val listener: TestCaseExecutionListener,
) : TestExecutionInterceptor {

   private val logger = Logger(TestFinishedInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      val result = test(testCase, scope)
      logger.log { Pair(testCase.name.testName, "Test result $result") }

      when (result) {
         is TestResult.Ignored -> listener.testIgnored(testCase, result.reason)
         else -> listener.testFinished(testCase, result)
      }

      return result
   }
}

