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
 * Wraps an execution function checking for assertion mode, if a [TestType.Test] and if enabled.
 */
internal class AssertionModeTestExecutionExtension(private val start: Long) : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->

      when (testCase.type) {
         TestType.Container -> test(context)
         TestType.Test -> {

            assertionCounter.reset()
            val result = test(context)
            when (result.status) {
               TestStatus.Success -> result
               else -> {
                  val warningMessage = "Test '${testCase.displayName}' did not invoke any assertions"
                  val mode = testCase.spec.assertions ?: testCase.spec.assertionMode() ?: configuration.assertionMode
                  when {
                     mode == AssertionMode.Error && assertionCounter.getAndReset() == 0 ->
                        createTestResult(timeInMillis() - start, ZeroAssertionsError(warningMessage))
                     mode == AssertionMode.Warn && assertionCounter.getAndReset() == 0 -> {
                        println("Warning: $warningMessage")
                        result
                     }
                     else -> result
                  }
               }
            }
         }
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
