package io.kotest.engine.spec.interceptor

import io.kotest.core.NamedTag
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.config.Configuration
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.filter.SpecFilter
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.engine.tags.runtimeTags
import io.kotest.mpp.annotation

/**
 * A [SpecFilter] which will ignore specs if they are annotated with @[RequiresTag]
 * and those tags are not present in the runtime tags.
 */
internal class RequiresTagSpecInterceptor(
   private val listener: TestEngineListener,
   private val configuration: Configuration,
   private val registry: ExtensionRegistry,
) : SpecRefInterceptor {

   override suspend fun intercept(
      fn: suspend (SpecRef) -> Map<TestCase, TestResult>
   ): suspend (SpecRef) -> Map<TestCase, TestResult> = { ref ->
      val annotation = ref.kclass.annotation<RequiresTag>()
      if (annotation == null) {
         fn(ref)
      } else {

         val requiredTags = annotation.values.map { NamedTag(it) }.toSet()
         val expr = configuration.runtimeTags().parse()

         val isActive = requiredTags.isEmpty() || expr.isActive(requiredTags)
         if (isActive) {
            fn(ref)
         } else {
            listener.specIgnored(ref.kclass, "Disabled by @RequiresTag")
            SpecExtensions(registry).ignored(ref.kclass, "Disabled by @RequiresTag")
            emptyMap()
         }
      }
   }
}
