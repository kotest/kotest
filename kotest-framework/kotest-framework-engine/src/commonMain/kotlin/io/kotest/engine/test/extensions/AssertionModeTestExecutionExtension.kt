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
internal class AssertionModeTestExecutionExtension(private val start: Long) : TestExecutionExtension {

   private fun mode(testCase: TestCase) =
      testCase.spec.assertions ?: testCase.spec.assertionMode() ?: configuration.assertionMode

   override suspend fun shouldApply(testCase: TestCase): Boolean {
      if (testCase.type == TestType.Container) return false
      val mode = mode(testCase)
      if (mode == AssertionMode.None) return false
      return true
   }

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      assertionCounter.reset()
      val result = test(context)

      val warningMessage = "Test '${testCase.displayName}' did not invoke any assertions"
      val mode = mode(testCase)

      when {
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
