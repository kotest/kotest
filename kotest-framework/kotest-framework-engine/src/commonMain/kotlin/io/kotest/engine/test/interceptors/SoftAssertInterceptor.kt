package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertSoftly
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.mpp.log

/**
 * Executes the test with assertSoftly if [assertSoftly] is enabled for this test
 * and if the [TestCase] is a [TestType.Test].
 */
internal class SoftAssertInterceptor() : TestExecutionInterceptor {

   private fun shouldApply(testCase: TestCase): Boolean {
      return testCase.type == TestType.Test && testCase.config.assertSoftly
   }

   override suspend fun intercept(
      test: suspend (TestCase, TestScope) -> TestResult
   ): suspend (TestCase, TestScope) -> TestResult = { testCase, context ->
      if (shouldApply(testCase)) {
         log { "SoftAssertInterceptor: Invoking test with soft assert" }
         assertSoftly { test(testCase, context) }
      } else {
         log { "SoftAssertInterceptor: Invoking test *without* soft assert" }
         test(testCase, context)
      }
   }
}
