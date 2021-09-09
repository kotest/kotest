package io.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.withCoroutineContext
import kotlin.coroutines.coroutineContext

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = CoroutineDispatcherInterceptor(defaultCoroutineDispatcherFactory)

/**
 * Switches execution onto a dispatcher provided by the given [CoroutineDispatcherFactory].
 */
class CoroutineDispatcherInterceptor(private val controller: CoroutineDispatcherFactory) : TestExecutionInterceptor {

   override suspend fun intercept(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {
      return { testCase, context ->
         controller.withDispatcher(testCase) {
            test(testCase, context.withCoroutineContext(coroutineContext))
         }
      }
   }
}
