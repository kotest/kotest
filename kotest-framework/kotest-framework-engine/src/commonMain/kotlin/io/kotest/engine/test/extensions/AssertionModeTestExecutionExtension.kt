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
import io.kotest.engine.test.createTestResult
import io.kotest.mpp.timeInMillis

/**
 * Wraps the test function checking for assertion mode, if the test is a [TestType.Test].
 */
internal class AssertionModeTestExecutionExtension(private val start: Long) : TestExecutionExtension {

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      assertionCounter.reset()
      val result = test(context)
      val mode = testCase.spec.assertions ?: testCase.spec.assertionMode() ?: configuration.assertionMode
      val warningMessage = "Test '${testCase.displayName}' did not invoke any assertions"

      when {
         // assertions mode has no effect on containers
         testCase.type == TestType.Container -> result
         // if we had an error anyway, we don't bother with this check
         result.status in listOf(TestStatus.Error, TestStatus.Failure) -> result
         // if we had assertions we're good
         assertionCounter.getAndReset() > 0 -> result
         // mode disabled
         mode == AssertionMode.Error -> createTestResult(timeInMillis() - start, ZeroAssertionsError(warningMessage))
         mode == AssertionMode.Warn -> {
            println("Warning: $warningMessage")
            result
         }
         else -> result
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
