package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.status.isEnabled
import io.kotest.mpp.log

/**
 * Checks the enabled status of a [TestCase] before invoking it.
 * If the test is disabled, then [TestResult.ignored] is returned.
 */
object EnabledCheckTestExecutionExtension : TestExecutionExtension {
   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      val enabled = testCase.isEnabled()
      when (enabled.isEnabled) {
         true -> {
            log { "${testCase.description.testPath()} is enabled" }
            test(context)
         }
         false -> {
            log { "${testCase.description.testPath()} is disabled" }
            TestResult.ignored(enabled)
         }
      }
   }
}
