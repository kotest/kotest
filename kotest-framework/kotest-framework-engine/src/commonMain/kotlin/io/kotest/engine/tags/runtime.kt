package io.kotest.engine.tags

import io.kotest.core.TagExpression
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.TagExtension

/**
 * Returns the runtime [TagExpression]'s by invoking all registered [TagExtension]s
 */
fun ProjectConfiguration.runtimeTagExpression(): TagExpression {
   val extensions = this.registry.all().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) TagExpression.Empty else
      extensions.map { it.tags() }
         .reduce { a, b -> a.combine(b) }
}
