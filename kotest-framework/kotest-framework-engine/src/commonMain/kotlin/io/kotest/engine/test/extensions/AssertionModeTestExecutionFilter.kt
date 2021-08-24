package io.kotest.engine.test.extensions

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.config.configuration
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType

/**
 * Wraps the test function checking for assertion mode, if the test is a [TestType.Test].
 */
internal object AssertionModeTestExecutionFilter : TestExecutionFilter {

   private fun mode(testCase: TestCase) =
      testCase.spec.assertions ?: testCase.spec.assertionMode() ?: configuration.assertionMode

   private fun shouldApply(testCase: TestCase): Boolean {
      if (testCase.type == TestType.Container) return false
      val mode = mode(testCase)
      if (mode == AssertionMode.None) return false
      return true
   }

   override suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      if (shouldApply(testCase)) apply(testCase, context, test) else test(testCase, context)
   }

   private suspend fun apply(
      testCase: TestCase,
      context: TestContext,
      test: suspend (TestCase, TestContext) -> TestResult
   ): TestResult {

      assertionCounter.reset()
      val result = test(testCase, context)

      val warningMessage = "Test '${testCase.displayName}' did not invoke any assertions"
      val mode = mode(testCase)

      return when {
         // if we had an error anyway, we don't bother with this check
         result.status in listOf(TestStatus.Error, TestStatus.Failure) -> result
         // if we had assertions we're good
         assertionCounter.getAndReset() > 0 -> result
         mode == AssertionMode.Error -> throw ZeroAssertionsError(warningMessage)
         mode == AssertionMode.Warn -> {
            println("Warning: $warningMessage")
            result
         }
         else -> result
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
