package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.test.TestResult

/**
 * Invokes any [BeforeSpecListener] and [AfterSpecListener] callbacks for the given spec.
 */
internal class BeforeAfterSpecCallbacksInterceptor(
   private val specExtensions: SpecExtensions,
) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {

      return try {
         // any errors in user callbacks cause us to abort the spec
         specExtensions.beforeSpec(spec)
         val results = next.invoke(spec)
         // any errors in user callbacks cause us to abort the spec
         specExtensions.afterSpec(spec).getOrThrow()
         results
      } catch (t: Throwable) {
         Result.failure(t)
      }
   }
}
