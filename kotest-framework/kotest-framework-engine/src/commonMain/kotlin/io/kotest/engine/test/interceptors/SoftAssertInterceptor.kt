package io.kotest.engine.test.interceptors

import io.kotest.assertions.assertSoftly
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.mpp.Logger

/**
 * Executes the test with assertSoftly if [assertSoftly] is enabled for this test
 * and if the [TestCase] is a [TestType.Test].
 */
internal class SoftAssertInterceptor() : TestExecutionInterceptor {

   private val logger = Logger(SoftAssertInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      if (testCase.type != TestType.Test) return test(testCase, scope)
      if (!testCase.config.assertSoftly) return test(testCase, scope)

      logger.log { Pair(testCase.name.testName, "Invoking test with soft assert") }
      return assertSoftly { test(testCase, scope) }
   }
}
