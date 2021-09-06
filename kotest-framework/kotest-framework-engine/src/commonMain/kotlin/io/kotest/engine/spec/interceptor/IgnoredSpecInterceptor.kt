package io.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.SpecRef
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.log

/**
 * Skips any spec marked with @[Ignored] annotation and notifies the test engine listener
 * that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
class IgnoredSpecInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {
   override suspend fun intercept(fn: suspend (SpecRef) -> Unit): suspend (SpecRef) -> Unit = { ref ->
      val isIgnored = ref.kclass.hasAnnotation<Ignored>()
      log { "IgnoredSpecInterceptor: ${ref.kclass} has @Ignored == $isIgnored" }
      if (isIgnored) {
         listener.specIgnored(ref.kclass)
      } else {
         fn(ref)
      }
   }
}
