package io.kotest.engine.test.extensions

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

/**
 * Executes the test with assertSoftly if global assert mode us enabled at the project level and if
 * this [TestCase] is a [TestType.Test].
 */
internal object GlobalSoftAssertTestExecutionExtension : TestExecutionExtension {

   override suspend fun shouldApply(testCase: TestCase): Boolean {
      return testCase.type == TestType.Test && configuration.globalAssertSoftly
   }

   override suspend fun execute(
      testCase: TestCase,
      test: suspend (TestContext) -> TestResult
   ): suspend (TestContext) -> TestResult = { context ->
      assertSoftly { test(context) }
   }
}
