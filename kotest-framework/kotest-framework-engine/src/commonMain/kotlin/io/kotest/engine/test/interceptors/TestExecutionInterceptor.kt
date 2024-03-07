package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope

/**
 * [TestExecutionInterceptor]s are invoked around a [TestCase].
 * They have the ability to skip tests, adjust the test case metadata, and
 * adjust test results.
 */
internal interface TestExecutionInterceptor {
   suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult
}
