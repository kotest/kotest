package io.kotest.engine.spec.interceptor.ref

import io.kotest.core.NamedTag
import io.kotest.core.annotation.RequiresTag
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.extensions.ExtensionRegistry
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.tags.TagExpressionBuilder
import io.kotest.engine.tags.isActive
import io.kotest.engine.tags.parse
import io.kotest.mpp.annotation

/**
 * A [SpecRefInterceptor] which will ignore specs if they are annotated with @[RequiresTag]
 * and those tags are not present in the runtime tag expression.
 */
internal class RequiresTagInterceptor(
   private val listener: TestEngineListener,
   private val registry: ExtensionRegistry,
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return when (val annotation = ref.kclass.annotation<RequiresTag>()) {
         null -> next.invoke(ref)
         else -> {
            val requiredTags = annotation.values.map { NamedTag(it) }.toSet()
            val expr = TagExpressionBuilder.build(projectConfigResolver).parse()
            if (requiredTags.isEmpty() || (expr != null && expr.isActive(requiredTags))) {
               next.invoke(ref)
            } else {
               runCatching { listener.specIgnored(ref.kclass, "Disabled by @RequiresTag") }
                  .flatMap { SpecExtensions(registry).ignored(ref.kclass, "Disabled by @RequiresTag") }
                  .map { emptyMap() }
            }
         }
      }
   }
}
