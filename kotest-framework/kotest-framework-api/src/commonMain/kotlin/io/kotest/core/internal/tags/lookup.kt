package io.kotest.core.internal.tags

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.test.TestCase

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
 * Returns the tags specified on the given class from the @Tags annotation if present.
 */
fun KClass<*>.tags(): Set<Tag> {
   val annotation = annotation<io.kotest.core.annotation.Tags>() ?: return emptySet()
   return annotation.values.map { NamedTag(it) }.toSet()
}

/**
 * Returns runtime active [Tag]'s by invocating all registered [TagExtension]s and combining
 * any returned tags into a [Tags] container.
 */
fun Configuration.activeTags(): Tags {
   val extensions = extensions().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) Tags.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}

/**
 * Returns all tags assigned to a [TestCase], taken from the test case config, spec inline function,
 * spec override function, or the spec class.
 */
fun TestCase.allTags(): Set<Tag> = this.config.tags + this.spec.declaredTags()
