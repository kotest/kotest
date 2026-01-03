package io.kotest.engine.spec.execution.enabled

import io.kotest.core.spec.SpecRef
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.tags.TagExpressionBuilder
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.engine.tags.parse

/**
 * Filters any [io.kotest.core.spec.Spec] that can be eagerly excluded based on
 * the @[io.kotest.core.annotation.Tags] annotation at the class level.
 */
internal class EagerlyExcludedByTagsSpecRefEnabledExtension(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefEnabledExtension {

   override fun isEnabled(ref: SpecRef): EnabledOrDisabled {

      val potentiallyActive = TagExpressionBuilder.build(projectConfigResolver)
         .parse()
         .isPotentiallyActive(ref.kclass, projectConfigResolver)

      return if (potentiallyActive != TagExpressionResult.Exclude) {
         EnabledOrDisabled.Enabled
      } else {
         EnabledOrDisabled.Disabled("Skipped by tags")
      }
   }
}
