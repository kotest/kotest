package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * A [TestExecutionInterceptor] that uses a [TestCoroutineDispatcher] as the coroutine
 * dispatcher for the test.
 *
 */
@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
actual class TestCoroutineDispatcherInterceptor : TestExecutionInterceptor {

   private val logger = Logger(TestCoroutineDispatcherInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      val currentDispatcher = coroutineContext[CoroutineDispatcher]
      return if (currentDispatcher is TestCoroutineDispatcher) {
         test(testCase, scope)
      } else {
         val dispatcher = TestCoroutineDispatcher()
         logger.log { Pair(testCase.name.testName, "Switching context to TestCoroutineDispatcher: $dispatcher") }
         withContext(dispatcher + CoroutineName("wibble")) {
            test(testCase, scope.withCoroutineContext(dispatcher))
         }
      }
   }
}
