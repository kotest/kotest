package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes the [TestEngineListener.specStarted] callbacks.
 */
internal class SpecStartedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: NextSpecRefInterceptor
   ): Result<Map<TestCase, TestResult>> {
      return runCatching { listener.specStarted(ref.kclass) }
         .flatMap { fn(ref) }
   }
}

