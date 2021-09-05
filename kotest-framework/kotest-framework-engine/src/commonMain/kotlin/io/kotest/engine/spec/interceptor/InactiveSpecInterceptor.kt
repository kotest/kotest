package io.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Skip
import io.kotest.engine.listener.TestEngineListener
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.hasAnnotation

/**
 * Skips any spec marked with @[Skip] annotation and notifies the test engine listener.
 *
 * Note: annotations are only available on the JVM.
 */
class InactiveSpecInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {
   override suspend fun intercept(fn: suspend (SpecRef) -> Unit): suspend (SpecRef) -> Unit = { ref ->
      if (ref.kclass.hasAnnotation<Skip>()) {
         listener.specIgnored(ref.kclass)
      } else {
         fn(ref)
      }
   }
}
