package io.kotest.engine.tags

import io.kotest.core.extensions.TagExtension
import io.kotest.engine.config.ProjectConfigResolver

/**
 * Returns the runtime [TagExpression]'s by invoking [TagExtension]s registered globally.
 */
internal object TagExpressionBuilder {

   fun build(projectConfigResolver: ProjectConfigResolver): TagExpression {
      val extensions = projectConfigResolver.extensions().filterIsInstance<TagExtension>()
      return if (extensions.isEmpty()) TagExpression.Empty else
         extensions.map { it.tags() }
            .reduce { a, b -> a.combine(b) }
   }
}
