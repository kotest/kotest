package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener

/**
 * A [SpecRefInterceptor] that invokes the [specStarted] test engine listener callbacks.
 */
internal class SpecStartedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return runCatching { listener.specStarted(ref.kclass) }
         .flatMap { fn(ref) }
   }
}

