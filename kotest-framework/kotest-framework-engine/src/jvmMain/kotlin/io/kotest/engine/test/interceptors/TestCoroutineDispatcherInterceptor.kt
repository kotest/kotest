package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * A [TestExecutionInterceptor] that switches execution to a [TestCoroutineDispatcher].
 */
@ExperimentalCoroutinesApi
actual class TestCoroutineDispatcherInterceptor : TestExecutionInterceptor {

   private val logger = Logger(this::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val dispatcher = TestCoroutineDispatcher()
      logger.log { Pair(testCase.name.testName, "Switching context to TestCoroutineDispatcher: $dispatcher") }
      return withContext(dispatcher) {
         test(testCase, scope.withCoroutineContext(dispatcher))
      }
   }
}
