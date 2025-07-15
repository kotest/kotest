package io.kotest.engine.spec.interceptor.ref.callbacks

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.test.TestResultBuilder
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
         .onSuccess {
            listener.specFinished(ref, TestResultBuilder.builder().withDuration(duration).build())
         }.onFailure {
            listener.specFinished(ref, TestResultBuilder.builder().withDuration(duration).withError(it).build())
         }
   }
}
