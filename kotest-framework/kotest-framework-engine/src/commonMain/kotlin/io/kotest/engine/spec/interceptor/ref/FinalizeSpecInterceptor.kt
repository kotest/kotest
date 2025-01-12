package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes any [FinalizeSpecListener.finalizeSpec] callbacks.
 */
internal class FinalizeSpecInterceptor(
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return next.invoke(ref)
         .onSuccess { specExtensions.finalizeSpec(ref.kclass, it, null) }
         .onFailure { specExtensions.finalizeSpec(ref.kclass, emptyMap(), it) }
   }
}
