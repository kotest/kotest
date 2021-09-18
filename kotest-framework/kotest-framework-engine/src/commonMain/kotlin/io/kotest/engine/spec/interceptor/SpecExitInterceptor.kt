package io.kotest.engine.spec.interceptor

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener

/**
 * A [SpecRefInterceptor] that invokes the specExit callback on the [TestEngineListener].
 * Any unhandled exception in the spec executor will be passed to this callback.
 */
internal class SpecExitInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      kotlin.runCatching { fn(ref) }
         .fold(
            {
               listener.specExit(ref.kclass, null)
               it
            },
            {
               listener.specExit(ref.kclass, it)
               emptyMap()
            }
         )
   }
}
