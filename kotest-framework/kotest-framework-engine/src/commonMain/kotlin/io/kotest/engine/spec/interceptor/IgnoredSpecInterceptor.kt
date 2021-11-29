package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.annotation.Ignored
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.Node
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import io.kotest.mpp.hasAnnotation

/**
 * Skips any spec marked with the [Ignored] annotation and notifies the [TestEngineListener]
 * that the spec is ignored.
 *
 * Note: annotations are only available on the JVM.
 */
internal class IgnoredSpecInterceptor(
   private val listener: TestEngineListener,
   registry: ExtensionRegistry,
) : SpecRefInterceptor {

   private val logger = Logger(IgnoredSpecInterceptor::class)
   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {

      val isIgnored = ref.kclass.hasAnnotation<Ignored>()
      logger.log { Pair(ref.kclass.bestName(), "@Ignored == $isIgnored") }

      return if (isIgnored) {
         runCatching { listener.executionIgnored(Node.Spec(ref.kclass), "Disabled by @Ignored") }
            .flatMap { extensions.ignored(ref.kclass, "Disabled by @Ignored") }
            .map { emptyMap() }
      } else {
         fn(ref)
      }
   }
}
