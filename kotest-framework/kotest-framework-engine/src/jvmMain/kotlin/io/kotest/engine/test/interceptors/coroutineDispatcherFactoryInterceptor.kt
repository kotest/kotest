package io.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.FixedThreadCoroutineDispatcherFactory
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.log
import kotlin.coroutines.coroutineContext

internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = CoroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory)

/**
 * Switches execution onto a dispatcher provided by a [CoroutineDispatcherFactory].
 */
internal class CoroutineDispatcherFactoryInterceptor(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
) : TestExecutionInterceptor {

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      val userFactory = testCase.spec.coroutineDispatcherFactory ?: testCase.spec.coroutineDispatcherFactory()
      val threads = testCase.spec.threads ?: testCase.spec.threads() ?: 1

      log { "CoroutineDispatcherFactoryInterceptor: userFactory=$userFactory; threads=$threads" }

      val f = when {
         userFactory != null -> userFactory
         threads > 1 -> FixedThreadCoroutineDispatcherFactory(threads, false)
         else -> defaultCoroutineDispatcherFactory
      }

      return f.withDispatcher(testCase) {
         test(testCase, scope.withCoroutineContext(coroutineContext))
      }
   }
}
