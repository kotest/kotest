package io.kotest.engine.spec.interceptor.ref.callbacks

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor

/**
 * A [SpecRefInterceptor] that invokes the [TestEngineListener.specStarted] callbacks.
 */
internal class SpecStartedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return runCatching { listener.specStarted(ref) }
         .onFailure { it.printStackTrace() }
         .flatMap { next.invoke(ref) }
   }
}

