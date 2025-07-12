package io.kotest.engine.spec.interceptor.ref.callbacks

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import kotlin.time.measureTimedValue

/**
 * A [SpecRefInterceptor] that invokes the [listener] test engine listener callbacks.
 * Any unhandled exception in the spec executor will be handled by this interceptor.
 */
internal class SpecFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      val (value, duration) = measureTimedValue {
         next.invoke(ref)
      }
      return value
         .onSuccess { listener.specFinished(ref, TestResult.Success(duration)) }
         .onFailure { listener.specFinished(ref, TestResult.Error(duration, it)) }
   }
}
