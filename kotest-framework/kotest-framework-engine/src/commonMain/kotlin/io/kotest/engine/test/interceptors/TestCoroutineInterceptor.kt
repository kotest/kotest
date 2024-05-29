package io.kotest.engine.test.interceptors

import io.kotest.core.coroutines.TestScopeElement
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.TestScope as CoroutinesTestScope

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
class TestCoroutineInterceptor : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      var result: TestResult = TestResult.Ignored
      logger.log { Pair(testCase.name.testName, "Switching context to coroutines runTest") }

      val testBody: suspend CoroutinesTestScope.() -> Unit = {
         withContext(TestScopeElement(this)) {
            result = test(testCase, scope.withCoroutineContext(coroutineContext))
         }
      }

      // Handle timeouts here to avoid the influence of default timeout set inside runTest
      val timeout = testCase.config.invocationTimeout.takeIf { testCase.type != TestType.Container }
         ?: testCase.config.timeout
      if (timeout != null) {
         runTest(timeout = timeout, testBody = testBody)
      } else {
         runTest(testBody = testBody)
      }
      return result
   }
}

