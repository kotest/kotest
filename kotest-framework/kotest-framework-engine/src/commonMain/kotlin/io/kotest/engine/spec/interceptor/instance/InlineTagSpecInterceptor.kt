package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.flatMap
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import io.kotest.engine.tags.TagExpressionBuilder
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse

/**
 * A [SpecInterceptor] that skips this [Spec] if it contains inline tags which don't satisfy
 * the current tag expression.
 */
internal class InlineTagSpecInterceptor(
   private val listener: TestEngineListener,
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecInterceptor {

   private val extensions = SpecExtensions(projectConfiguration.registry)

   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      val allTags = spec.tags() + spec.appliedTags()
      val potentiallyActive = TagExpressionResult.Exclude != TagExpressionBuilder.build(projectConfigResolver)
         .parse()
         .isPotentiallyActive(allTags)

      return if (potentiallyActive) next.invoke(spec) else {
         val reason = "Ignored due to tags in spec: ${allTags.joinToString(", ")}"
         runCatching { listener.specIgnored(spec::class, reason) }
            .flatMap { extensions.ignored(spec::class, reason) }
            .map { emptyMap() }
      }
   }
}

