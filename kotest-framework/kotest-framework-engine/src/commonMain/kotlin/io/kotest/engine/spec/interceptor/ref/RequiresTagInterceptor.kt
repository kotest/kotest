package io.kotest.engine.spec.interceptor.ref

import io.kotest.common.flatMap
import io.kotest.core.NamedTag
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.annotation.requirestag.wrapper
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.engine.tags.runtimeTagExpression
import io.kotest.mpp.annotation

/**
 * A [SpecRefInterceptor] which will ignore specs if they are annotated with @[RequiresTag]
 * and those tags are not present in the runtime tags.
 */
internal class RequiresTagInterceptor(
   private val listener: TestEngineListener,
   private val configuration: ProjectConfiguration,
   private val registry: ExtensionRegistry,
) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return when (val annotation = ref.kclass.annotation<RequiresTag>()) {
         null -> fn(ref)
         else -> {
            val requiredTags = annotation.wrapper.map { NamedTag(it) }.toSet()
            val expr = configuration.runtimeTagExpression().parse()
            if (requiredTags.isEmpty() || (expr != null && expr.isActive(requiredTags))) {
               fn(ref)
            } else {
               runCatching { listener.specIgnored(ref.kclass, "Disabled by @RequiresTag") }
                  .flatMap { SpecExtensions(registry).ignored(ref.kclass, "Disabled by @RequiresTag") }
                  .map { emptyMap() }
            }
         }
      }
   }
}
