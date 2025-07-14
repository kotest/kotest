package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * Invokes any [AfterSpecListener] callbacks for the given spec.
 *
 * These listeners are not invoked if no tests were executed for the spec (ie, all tests were ignored, or the
 * spec has no tests defined), or if any [io.kotest.core.listeners.BeforeSpecListener]s failed.
 */
internal class AfterSpecListenerInterceptor(
   private val specContext: SpecContext,
   private val specExtensions: SpecExtensions,
) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {

      // we only invoke after spec listeners, if we determined that before spec listeners should have run

      return next.invoke(spec).flatMap { results ->
         if (specContext.beforeSpecInvoked.get()) {
            specExtensions
               .afterSpec(spec)
               .map { results }
         } else {
            Result.success(results)
         }
      }
   }
}
