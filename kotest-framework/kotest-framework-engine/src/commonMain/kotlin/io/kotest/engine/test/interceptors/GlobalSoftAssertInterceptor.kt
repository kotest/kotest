package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.mpp.log

/**
 * Executes the test with assertSoftly if global assert mode us enabled at the project level and if
 * this [TestCase] is a [TestType.Test].
 */
internal class GlobalSoftAssertInterceptor(private val configuration: Configuration) : TestExecutionInterceptor {

   private fun shouldApply(testCase: TestCase): Boolean {
      return testCase.type == TestType.Test && configuration.globalAssertSoftly
   }

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->
      if (shouldApply(testCase)) {
         log { "GlobalSoftAssertInterceptor: Invoking test with soft assert" }
         assertSoftly { test(testCase, context) }
      } else {
         log { "GlobalSoftAssertInterceptor: Invoking test without soft assert" }
         test(testCase, context)
      }
   }
}
