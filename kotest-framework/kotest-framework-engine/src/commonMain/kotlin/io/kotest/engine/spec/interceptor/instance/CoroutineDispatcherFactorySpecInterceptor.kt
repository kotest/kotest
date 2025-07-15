package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.Logger
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.mpp.bestName
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
internal class CoroutineDispatcherFactorySpecInterceptor(
   private val specConfigResolver: SpecConfigResolver,
) : SpecInterceptor {

   private val logger = Logger(CoroutineDispatcherFactorySpecInterceptor::class)

   override suspend fun intercept(spec: Spec, next: NextSpecInterceptor): Result<Map<TestCase, TestResult>> {

      val currentDispatcher = coroutineContext[CoroutineDispatcher]
      // we don't override if we've set a test dispatcher on this already
      return if (currentDispatcher is TestDispatcher) {
         next.invoke(spec)
      } else {

         val userFactory = specConfigResolver.coroutineDispatcherFactory(spec)
         if (userFactory == null) {
            logger.log { Pair(spec::class.bestName(), "No CoroutineDispatcherFactory set") }
            next.invoke(spec)
         } else {
            logger.log { Pair(spec::class.bestName(), "Switching dispatcher using factory $userFactory") }
            userFactory.withDispatcher(spec) {
               next.invoke(spec)
            }
         }
      }
   }
}
