package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.toTestResult
import kotlin.time.Duration

/**
 * A [SpecRefInterceptor] that invokes the [specFinished] test engine listener callbacks.
 * Any unhandled exception in the spec executor will be handled by this interceptor.
 */
internal class SpecFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return fn(ref)
         .onSuccess { listener.executionFinished(Node.Spec(ref.kclass), TestResult.success) }
         .onFailure { listener.executionFinished(Node.Spec(ref.kclass), it.toTestResult(Duration.ZERO)) }
   }
}
