package io.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension

/**
 * Returns runtime active [Tag]'s by invoking all registered [TagExtension]s and combining
 * any returned tags into a [TagExpression] container.
 */
fun Configuration.runtimeTags(): TagExpression {
   val extensions = this.registry().all().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) TagExpression.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}
