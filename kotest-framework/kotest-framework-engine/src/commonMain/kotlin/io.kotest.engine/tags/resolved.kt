package io.kotest.engine.tags

import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension

/**
 * Returns the current state of [Tags] by combining any tags returned from currently
 * registered [TagExtension]s.
 */
fun Configuration.resolvedTags(): Tags {
   val extensions = this.extensions().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) Tags.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}
