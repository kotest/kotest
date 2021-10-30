package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener

/**
 * A [SpecRefInterceptor] that invokes the spec started and finished test engine listener callbacks.
 * Any unhandled exception in the spec executor will be handled by this interceptor.
 */
internal class SpecStartedFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      runCatching { listener.specStarted(ref.kclass) }
         .flatMap { runCatching { fn(ref) } }
         .fold(
            {
               listener.specFinished(ref.kclass, null)
               it
            },
            {
               listener.specFinished(ref.kclass, it)
               emptyMap()
            }
         )
   }
}
