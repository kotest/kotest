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

   object Noop : TestExecutionInterceptor {
      override suspend fun intercept(
         testCase: TestCase,
         scope: TestScope,
         test: suspend (TestCase, TestScope) -> TestResult
      ): TestResult {
         return test(testCase, scope)
      }

   }

   suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult
}
