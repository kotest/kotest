package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.timeInMillis
import kotlin.time.Duration.Companion.milliseconds

/**
 * A [SpecRefInterceptor] that invokes the [specFinished] test engine listener callbacks.
 * Any unhandled exception in the spec executor will be handled by this interceptor.
 */
internal class SpecFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val start = timeInMillis()
      val value = fn(ref)
      val duration = (timeInMillis() - start).milliseconds
      return value
         .onSuccess { listener.specFinished(ref.kclass, TestResult.Success(duration)) }
         .onFailure { listener.specFinished(ref.kclass, TestResult.Error(duration, it)) }
   }
}
