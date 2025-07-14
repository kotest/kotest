package io.kotest.engine.spec.interceptor.ref.callbacks

import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes any [PrepareSpecListener.prepareSpec] callbacks.
 */
internal class PrepareSpecInterceptor(private val specExtensions: SpecExtensions) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return specExtensions
         .prepareSpec(ref.kclass)
         .flatMap { next.invoke(ref) }
   }
}
