package io.kotest.engine.spec.interceptor.instance

import io.kotest.engine.flatMap
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse
import io.kotest.engine.tags.runtimeTagExpression

/**
 * A [SpecInterceptor] that skips this [Spec] if it contains inline tags which don't satisfy
 * the current tag expression.
 */
internal class InlineTagSpecInterceptor(
   private val listener: TestEngineListener,
   private val projectConfiguration: ProjectConfiguration,
) : SpecInterceptor {

   private val extensions = SpecExtensions(projectConfiguration.registry)

   override suspend fun intercept(
      spec: Spec,
      fn: NextSpecInterceptor
   ): Result<Map<TestCase, TestResult>> {
      val allTags = spec.tags() + spec.appliedTags()
      val potentiallyActive = TagExpressionResult.Exclude != projectConfiguration
         .runtimeTagExpression()
         .parse()
         .isPotentiallyActive(allTags)

      return if (potentiallyActive) fn(spec) else {
         val reason = "Ignored due to tags in spec: ${allTags.joinToString(", ")}"
         runCatching { listener.specIgnored(spec::class, reason) }
            .flatMap { extensions.ignored(spec::class, reason) }
            .map { emptyMap() }
      }
   }
}

