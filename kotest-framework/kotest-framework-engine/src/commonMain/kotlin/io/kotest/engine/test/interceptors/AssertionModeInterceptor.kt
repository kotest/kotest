package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

/**
 * Wraps the test function checking for assertion mode, if the test is a [TestType.Test].
 */
internal class AssertionModeInterceptor() : TestExecutionInterceptor {

   private fun shouldApply(testCase: TestCase): Boolean {
      if (testCase.type == TestType.Container) return false
      if (testCase.config.assertionMode == AssertionMode.None) return false
      return true
   }

   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->
      if (shouldApply(testCase)) apply(testCase, context, test) else test(testCase, context)
   }

   private suspend fun apply(
      testCase: TestCase,
      context: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      assertionCounter.reset()
      val result = test(testCase, context)

      val warningMessage = "Test '${testCase.name.testName}' did not invoke any assertions"

      return when {
         // if we had an error anyway, we don't bother with this check
         result.isErrorOrFailure -> result
         // if we had assertions we're good
         assertionCounter.getAndReset() > 0 -> result
         testCase.config.assertionMode == AssertionMode.Error -> throw ZeroAssertionsError(warningMessage)
         testCase.config.assertionMode == AssertionMode.Warn -> {
            println("Warning: $warningMessage")
            result
         }
         else -> result
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
