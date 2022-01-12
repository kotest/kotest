package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * A [TestExecutionInterceptor] that installs a [StandardTestDispatcher] as the coroutine
 * dispatcher for the test.
 *
 * If the current dispatcher is already a [TestDispatcher] then this interceptor is a no-op.
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
      return if (currentDispatcher is TestDispatcher) {
         test(testCase, scope)
      } else {
         val dispatcher = StandardTestDispatcher()
         logger.log { Pair(testCase.name.testName, "Switching context to StandardTestDispatcher: $dispatcher") }
         withContext(dispatcher + CoroutineName("wibble")) {
            test(testCase, scope.withCoroutineContext(dispatcher))
         }
      }
   }
}
