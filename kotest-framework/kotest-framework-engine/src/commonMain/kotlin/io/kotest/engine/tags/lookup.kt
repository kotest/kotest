package io.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension

/**
 * Returns all runtime tags when invoked, wrapping into an instance of [Tags].
 */
interface TagProvider {
   fun tags(): Tags
}

/**
 * Implementation of [TagProvider] that uses a [Configuration] to provide those tags.
 */
class ConfigurationTagProvider(private val configuration: Configuration) : TagProvider {
   override fun tags(): Tags = configuration.activeTags()
}



/**
 * Returns runtime active [Tag]'s by invocating all registered [TagExtension]s and combining
 * any returned tags into a [Tags] container.
 */
fun Configuration.activeTags(): Tags {
   val extensions = this.extensions().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) Tags.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}
