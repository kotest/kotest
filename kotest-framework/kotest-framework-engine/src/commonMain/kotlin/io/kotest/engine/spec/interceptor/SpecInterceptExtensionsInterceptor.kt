package io.kotest.engine.spec.interceptor

import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.spec.Spec
import io.kotest.mpp.log

/**
 * A [SpecExecutionInterceptor] that executes all [SpecInterceptExtension]s.
 */
class SpecInterceptExtensionsInterceptor(
   private val extensions: List<SpecInterceptExtension>
) : SpecExecutionInterceptor {

   override suspend fun intercept(fn: suspend (Spec) -> Unit): suspend (Spec) -> Unit = { spec ->
      log { "SpecInterceptExtensionsInterceptor: Intercepting spec with ${extensions.size} extensions [$extensions]" }
      val initial: suspend () -> Unit = { fn(spec) }
      interceptSpec(spec, extensions, initial)
   }

   private suspend fun interceptSpec(
      spec: Spec,
      remaining: List<SpecInterceptExtension>,
      run: suspend () -> Unit
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
