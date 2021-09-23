package io.kotest.engine.spec.interceptor

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.log

/**
 * A [SpecExecutionInterceptor] that executes all [SpecExtension]s.
 */
internal class SpecInterceptExtensionsInterceptor(
   private val extensions: List<SpecExtension>
) : SpecExecutionInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->
      log { "SpecInterceptExtensionsInterceptor: Intercepting spec with ${extensions.size} extensions [$extensions]" }
      var results = emptyMap<TestCase, TestResult>()
      val initial: suspend () -> Unit = { results = fn(spec) }
      interceptSpec(spec, extensions, initial)
      results
   }

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecExtension>,
      run: suspend () -> Unit,
   ) {
      when {
         remaining.isEmpty() -> run()
         else -> {
            remaining.first().intercept(spec::class) {
               remaining.first().intercept(spec) {
                  interceptSpec(spec, remaining.drop(1), run)
               }
            }
         }
      }
   }
}
