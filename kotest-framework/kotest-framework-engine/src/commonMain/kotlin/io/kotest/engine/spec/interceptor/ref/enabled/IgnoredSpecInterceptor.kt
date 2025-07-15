package io.kotest.engine.spec.interceptor.ref.enabled

import io.kotest.core.Logger
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.name
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.annotation
import io.kotest.mpp.hasAnnotation

/**
 * Skips any spec marked with the [io.kotest.core.annotation.Ignored] annotation and notifies the [io.kotest.engine.listener.TestEngineListener]
 * that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class IgnoredSpecInterceptor(
   private val listener: TestEngineListener,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   private val logger = Logger(IgnoredSpecInterceptor::class)

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val isIgnored = ref.kclass.hasAnnotation<Ignored>()
      logger.log { Pair(ref.name(), "@Ignored == $isIgnored") }

      return if (isIgnored) {
         val reason = ref.kclass.annotation<Ignored>()?.reason.let {
            if (it.isNullOrBlank())
               "Disabled by @Ignored"
            else
               """Disabled by @Ignored(reason="$it")"""
         }

         runCatching { listener.specIgnored(ref.kclass, reason) }
            .flatMap { specExtensions.ignored(ref.kclass, reason) }
            .map { emptyMap() }
      } else {
         next.invoke(ref)
      }
   }
}
