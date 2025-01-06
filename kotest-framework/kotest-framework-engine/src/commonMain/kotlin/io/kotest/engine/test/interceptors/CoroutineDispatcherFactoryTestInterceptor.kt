package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.scopes.withCoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.coroutineContext

/**
 * Switches execution onto a dispatcher provided by a [io.kotest.engine.coroutines.CoroutineDispatcherFactory].
 *
 * If the coroutine is an instance of [TestDispatcher] then the coroutine will not be changed.
 *
 * Note: This interceptor should run before before/after callbacks so they are executed in the right context.
 */
internal class CoroutineDispatcherFactoryTestInterceptor(
   private val testConfigResolver: TestConfigResolver,
) : TestExecutionInterceptor {

   private val logger = Logger(CoroutineDispatcherFactoryTestInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val currentDispatcher = coroutineContext[CoroutineDispatcher]
      // we don't override if we've set a test dispatcher on this already
      return if (currentDispatcher is TestDispatcher) {
         test(testCase, scope)
      } else {

         val userFactory = testConfigResolver.coroutineDispatcherFactory(testCase)
         if (userFactory == null) {
            logger.log { Pair(testCase.name.name, "No CoroutineDispatcherFactory set") }
            test(testCase, scope)
         } else {
            logger.log { Pair(testCase.name.name, "Switching dispatcher using factory $userFactory") }
            userFactory.withDispatcher(testCase) {
               test(testCase, scope.withCoroutineContext(coroutineContext))
            }
         }
      }
   }
}
