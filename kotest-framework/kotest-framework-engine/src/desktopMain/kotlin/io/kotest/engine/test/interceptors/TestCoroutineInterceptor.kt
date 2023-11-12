package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.test.runTest

actual class TestCoroutineInterceptor : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      var result: TestResult = TestResult.Ignored
      logger.log { Pair(testCase.name.testName, "Switching context to coroutines runTest") }
      runTest {
         result = test(testCase, scope.withCoroutineContext(this.coroutineContext))
      }
      return result
   }
}
