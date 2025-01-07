package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.annotation.EnabledIf
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
 * Evaluates any spec annotated with [EnabledIf] if the condition fails, skips the spec
 * and notifies the [TestEngineListener] that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class EnabledIfInterceptor(
   private val listener: TestEngineListener,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val annotation = ref.kclass
         .annotation<EnabledIf>(IncludingAnnotations, IncludingSuperclasses)
         ?.enabledIf
         ?.newInstanceNoArgConstructor()

      val enabled = annotation?.enabled(ref.kclass) != false

      return if (enabled) {
         next.invoke(ref)
      } else {
         val message = "Disabled by @EnabledIf ($annotation)"
         runCatching { listener.specIgnored(ref.kclass, message) }
            .flatMap { specExtensions.ignored(ref.kclass, message) }
            .map { emptyMap() }
      }
   }
}
