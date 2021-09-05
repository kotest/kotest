package io.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Skip
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.log

/**
 * Skips any spec marked with @[Skip] annotation and notifies the test engine listener.
 *
 * Note: annotations are only available on the JVM.
 */
class InactiveSpecInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {
   override suspend fun intercept(fn: suspend (SpecRef) -> Unit): suspend (SpecRef) -> Unit = { ref ->
      val hasAnnotation = ref.kclass.hasAnnotation<Skip>()
      log { "InactiveSpecInterceptor: ${ref.kclass} has @Skip == $hasAnnotation" }
      if (hasAnnotation) {
         listener.specIgnored(ref.kclass)
      } else {
         fn(ref)
      }
   }
}
