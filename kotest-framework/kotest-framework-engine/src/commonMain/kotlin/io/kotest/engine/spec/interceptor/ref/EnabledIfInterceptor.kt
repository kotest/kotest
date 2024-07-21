package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.wrapper
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.mpp.annotation
import io.kotest.mpp.newInstanceNoArgConstructor

/**
 * Evaluates any spec annotated with [EnabledIf] if the condition fails, skips the spec
 * and notifies the [TestEngineListener] that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class EnabledIfInterceptor(
   private val listener: TestEngineListener,
   registry: ExtensionRegistry,
) : SpecRefInterceptor {

   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {

      val enabled = ref.kclass
         .annotation<EnabledIf>()
         ?.wrapper
         ?.newInstanceNoArgConstructor()
         ?.enabled(ref.kclass) ?: true

      return if (enabled) {
         fn(ref)
      } else {
         runCatching { listener.specIgnored(ref.kclass, "Disabled by @EnabledIf") }
            .flatMap { extensions.ignored(ref.kclass, "Disabled by @EnabledIf") }
            .map { emptyMap() }
      }
   }
}
