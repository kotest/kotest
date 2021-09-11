package io.kotest.engine.spec.interceptor

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.log

/**
 * Skips any spec marked with the [Ignored] annotation and notifies the [TestEngineListener]
 * that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class IgnoredSpecInterceptor(private val listener: TestEngineListener) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      val isIgnored = ref.kclass.hasAnnotation<Ignored>()
      log { "IgnoredSpecInterceptor: ${ref.kclass} has @Ignored == $isIgnored" }
      if (isIgnored) {
         listener.specIgnored(ref.kclass)
         emptyMap()
      } else {
         fn(ref)
      }
   }
}
