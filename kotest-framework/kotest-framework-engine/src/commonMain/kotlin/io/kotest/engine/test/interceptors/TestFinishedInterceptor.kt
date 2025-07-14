package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestExtensions

internal class TestFinishedInterceptor(
   private val listener: TestCaseExecutionListener,
   private val testExtensions: TestExtensions,
) : TestExecutionInterceptor {

   private val logger = Logger(TestFinishedInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val result = test(testCase, scope)
      logger.log { Pair(testCase.name.name, "Test result $result") }

      when (result) {
         is TestResult.Ignored -> {
            listener.testIgnored(testCase, result.reason)
            testExtensions.ignoredTestListenersInvocation(testCase, result.reason)
         }

         else -> listener.testFinished(testCase, result)
      }

      return result
   }
}

