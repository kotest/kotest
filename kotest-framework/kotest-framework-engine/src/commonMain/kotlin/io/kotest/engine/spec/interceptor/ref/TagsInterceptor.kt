package io.kotest.engine.spec.interceptor.ref

import io.kotest.engine.flatMap
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse
import io.kotest.engine.tags.runtimeTagExpression

/**
 * Filters any [Spec] that can be eagerly excluded based on the @[io.kotest.core.annotation.Tags]
 * annotation at the class level.
 */
internal class TagsInterceptor(
   private val listener: TestEngineListener,
   private val conf: ProjectConfiguration,
) : SpecRefInterceptor {

   private val extensions = SpecExtensions(conf.registry)

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val potentiallyActive = TagExpressionResult.Exclude != conf
         .runtimeTagExpression()
         .parse()
         .isPotentiallyActive(ref.kclass, conf)

      return if (potentiallyActive) {
         fn(ref)
      } else {
         runCatching { listener.specIgnored(ref.kclass, null) }
            .flatMap { extensions.ignored(ref.kclass, "Skipped by tags") }
            .map { emptyMap() }
      }
   }
}
