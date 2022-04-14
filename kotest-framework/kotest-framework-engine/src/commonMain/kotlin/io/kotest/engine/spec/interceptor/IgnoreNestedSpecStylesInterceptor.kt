package io.kotest.engine.spec.interceptor

import io.kotest.common.flatMap
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.bestName
import io.kotest.mpp.log

/**
 * Filters [Spec]'s that are not compatible on platforms that disallow nested tests.
 */
internal class IgnoreNestedSpecStylesInterceptor(
   private val listener: TestEngineListener,
   registry: ExtensionRegistry,
) : SpecInterceptor {

   private val extensions = SpecExtensions(registry)

   override suspend fun intercept(
      spec: SpecContainer,
      fn: suspend (SpecContainer) -> Result<Pair<SpecContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecContainer, Map<TestCase, TestResult>>> {

      fun isValid(spec: Spec) = when (spec) {
         is FunSpec, is ExpectSpec, is FeatureSpec, is ShouldSpec, is StringSpec -> true
         else -> false
      }

      return if (isValid(spec.spec)) {
         fn(spec)
      } else {
         log { "IgnoreNestedSpecStylesInterceptor: Marking ${spec.spec::class.bestName()} as inactive due to platform limitations" }
         println("WARN: kotest-js only supports top level tests due to underlying platform limitations. '${spec.spec::class.bestName()}' has been marked as ignored")
         runCatching { listener.specIgnored(spec.spec::class, "Disabled due to platform limitations") }
            .flatMap { extensions.ignored(spec.spec::class, "Disabled due to platform limitations") }
            .map { spec.disable() to emptyMap() }
      }
   }
}
