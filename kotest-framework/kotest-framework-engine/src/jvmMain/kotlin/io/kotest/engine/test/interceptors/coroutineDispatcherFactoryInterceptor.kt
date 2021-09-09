package io.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.FixedThreadCoroutineDispatcherFactory
import io.kotest.engine.test.withCoroutineContext
import kotlin.coroutines.coroutineContext

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = CoroutineDispatcherInterceptor(defaultCoroutineDispatcherFactory)

/**
 * Switches execution onto a dispatcher provided by the given [CoroutineDispatcherFactory].
 */
class CoroutineDispatcherInterceptor(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
) : TestExecutionInterceptor {

   override suspend fun intercept(test: suspend (TestCase, TestContext) -> TestResult): suspend (TestCase, TestContext) -> TestResult {
      return { testCase, context ->

         val userFactory = testCase.spec.coroutineDispatcherFactory ?: testCase.spec.coroutineDispatcherFactory()
         val threads = testCase.spec.threads ?: testCase.spec.threads() ?: 1

         val f = when {
            userFactory != null -> userFactory
            threads > 1 -> FixedThreadCoroutineDispatcherFactory(threads, false)
            else -> defaultCoroutineDispatcherFactory
         }

         f.withDispatcher(testCase) {
            test(testCase, context.withCoroutineContext(coroutineContext))
         }
      }
   }
}
