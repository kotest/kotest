package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes any [PrepareSpecListener.prepareSpec] callbacks.
 */
internal class PrepareSpecInterceptor(registry: ExtensionRegistry) : SpecRefInterceptor {

   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: NextSpecRefInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      return extensions
         .prepareSpec(ref.kclass)
         .flatMap { fn(ref) }
   }
}
