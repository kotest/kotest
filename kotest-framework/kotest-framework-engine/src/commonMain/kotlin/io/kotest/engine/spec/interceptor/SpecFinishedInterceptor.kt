package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import kotlin.time.measureTimedValue

/**
 * A [SpecRefInterceptor] that invokes the [specFinished] test engine listener callbacks.
 * Any unhandled exception in the spec executor will be handled by this interceptor.
 */
internal class SpecFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRefContainer,
      fn: suspend (SpecRefContainer) -> Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecRefContainer, Map<TestCase, TestResult>>> {
      val (value, duration) = measureTimedValue { fn(ref) }
      return value
         .onSuccess { listener.specFinished(ref.specRef.kclass, TestResult.Success(duration)) }
         .onFailure { listener.specFinished(ref.specRef.kclass, TestResult.Error(duration, it)) }
   }
}
