package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * A [TestExecutionInterceptor] that installs a [UnconfinedTestDispatcher] as the coroutine
 * dispatcher for the test.
 *
 * If the current dispatcher is already a [TestDispatcher] then this interceptor is a no-op.
 */
@ExperimentalCoroutinesApi
internal class TestDispatcherInterceptor : TestExecutionInterceptor {

   private val logger = Logger(TestDispatcherInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {
      return when (coroutineContext[CoroutineDispatcher]) {
         is TestDispatcher -> test(testCase, scope)
         else -> {
            val dispatcher = UnconfinedTestDispatcher()
            logger.log { Pair(testCase.name.testName, "Switching context to StandardTestDispatcher: $dispatcher") }
            withContext(dispatcher + CoroutineName("wibble")) {
               test(testCase, scope.withCoroutineContext(dispatcher))
            }
         }
      }
   }
}
