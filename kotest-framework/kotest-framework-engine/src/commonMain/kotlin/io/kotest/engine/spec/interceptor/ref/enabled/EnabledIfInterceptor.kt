package io.kotest.engine.spec.interceptor.ref.enabled

import io.kotest.common.JVMOnly
import io.kotest.common.reflection.IncludingAnnotations
import io.kotest.common.reflection.IncludingSuperclasses
import io.kotest.common.reflection.annotation
import io.kotest.common.reflection.instantiations
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.test.TestResult

/**
 * Evaluates any spec annotated with [io.kotest.core.annotation.EnabledIf] if the condition fails, skips the spec
 * and notifies the [io.kotest.engine.listener.TestEngineListener] that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
@JVMOnly
internal class EnabledIfInterceptor(
   private val listener: TestEngineListener,
   private val specExtensions: SpecExtensions,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {

      val annotation = ref.kclass
         .annotation<EnabledIf>(IncludingAnnotations, IncludingSuperclasses)
         ?.condition
         ?.let { instantiations.newInstanceNoArgConstructorOrObjectInstance(it) }

      val result = annotation?.evaluate(ref.kclass)

      // null is fine, just means there was no annotation
      return if (result != false) {
         next.invoke(ref)
      } else {
         val message = "Disabled by @EnabledIf ($annotation)"
         runCatching { listener.specIgnored(ref.kclass, message) }
            .flatMap { specExtensions.ignored(ref.kclass, message) }
            .map { emptyMap() }
      }
   }
}
