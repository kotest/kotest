package io.kotest.engine.test.interceptors

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.concurrency.FixedThreadCoroutineDispatcherFactory
import io.kotest.engine.test.scopes.withCoroutineContext
import io.kotest.mpp.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlin.coroutines.coroutineContext

@ExperimentalStdlibApi
internal actual fun coroutineDispatcherFactoryInterceptor(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
): TestExecutionInterceptor = CoroutineDispatcherFactoryInterceptor(defaultCoroutineDispatcherFactory)

/**
 * Switches execution onto a dispatcher provided by a [CoroutineDispatcherFactory].
 *
 * If the coroutine is an instance of [TestDispatcher] then the coroutine will not be changed.
 */
@ExperimentalStdlibApi
internal class CoroutineDispatcherFactoryInterceptor(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory
) : TestExecutionInterceptor {

   private val logger = Logger(CoroutineDispatcherFactoryInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {

      val currentDispatcher = coroutineContext[CoroutineDispatcher]
      return if (currentDispatcher is TestCoroutineDispatcher) {
         test(testCase, scope)
      } else {

         val userFactory = testCase.spec.coroutineDispatcherFactory ?: testCase.spec.coroutineDispatcherFactory()
         val threads = testCase.spec.threads ?: testCase.spec.threads() ?: 1

         logger.log { Pair(testCase.name.testName, "userFactory=$userFactory; threads=$threads") }

         val f = when {
            userFactory != null -> userFactory
            threads > 1 -> FixedThreadCoroutineDispatcherFactory(threads, false)
            else -> defaultCoroutineDispatcherFactory
         }

         logger.log { Pair(testCase.name.testName, "Switching dispatcher using factory $f") }
         f.withDispatcher(testCase) {
            test(testCase, scope.withCoroutineContext(coroutineContext))
         }
      }
   }
}
