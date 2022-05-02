package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
actual class TestCoroutineInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult = test(testCase, scope)
}
