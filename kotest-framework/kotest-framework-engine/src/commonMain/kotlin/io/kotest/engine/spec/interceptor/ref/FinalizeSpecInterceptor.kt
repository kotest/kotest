package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes any [FinalizeSpecListener.finalizeSpec] callbacks.
 */
internal class FinalizeSpecInterceptor(
   registry: ExtensionRegistry,
) : SpecRefInterceptor {

   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return fn(ref)
         .onSuccess { extensions.finalizeSpec(ref.kclass, it, null) }
         .onFailure { extensions.finalizeSpec(ref.kclass, emptyMap(), it) }
   }
}
