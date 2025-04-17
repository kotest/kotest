package io.kotest.engine.spec.interceptor.ref.enabled

import io.kotest.core.annotation.DisabledIf
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.newInstanceNoArgConstructor
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.IncludingAnnotations
import io.kotest.mpp.IncludingSuperclasses
import io.kotest.mpp.annotation

/**
 * Evaluates any spec annotated with [io.kotest.core.annotation.DisabledIf] if the condition is true, skips the spec
 * and notifies the [io.kotest.engine.listener.TestEngineListener] that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class DisabledIfInterceptor(
   private val listener: TestEngineListener,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val annotation = ref.kclass
         .annotation<DisabledIf>(IncludingAnnotations, IncludingSuperclasses)
         ?.condition
         ?.newInstanceNoArgConstructor()

      val result = annotation?.evaluate(ref.kclass)

      // null is ok, just means there was no annotation
      return if (result == true) {
         val message = "Disabled by @DisabledIf ($annotation)"
         runCatching { listener.specIgnored(ref.kclass, message) }
            .flatMap { specExtensions.ignored(ref.kclass, message) }
            .map { emptyMap() }
      } else {
         next.invoke(ref)
      }
   }
}
