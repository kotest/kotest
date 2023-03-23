package io.kotest.engine.test.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.coroutines.TestScopeContainer
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

/**
 * A [TestExecutionInterceptor] that uses [runTest] from the coroutine library
 * to install test dispatchers.
 *
 * This setting cannot be nested.
 */
@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
@OptIn(KotestInternal::class)
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
         withContext(TestScopeContainer(this)) {
            result = test(testCase, scope.withCoroutineContext(coroutineContext))
         }
      }
      return result
   }
}
