package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener

/**
 * A [SpecRefInterceptor] that is the final stage in the spec execution pipeline.
 *
 * It will invoke [specFinished] on the [TestEngineListener].
 * Any unhandled exception in the spec executor will be passed to this callback.
 */
internal class SpecFinishedInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      runCatching { fn(ref) }
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
