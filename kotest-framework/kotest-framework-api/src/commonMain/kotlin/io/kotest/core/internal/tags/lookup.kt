package io.kotest.core.internal.tags

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.TagExtension
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Returns the current state of [Tags] by combining any tags returned from currently
 * registered [TagExtension]s.
 */
fun Configuration.resolvedTags(): Tags {
   val extensions = this.extensions().filterIsInstance<TagExtension>()
   return if (extensions.isEmpty()) Tags.Empty else extensions.map { it.tags() }.reduce { a, b -> a.combine(b) }
}

/**
 * Returns the tags specified on the given class from the @Tags annotation if present.
 */
fun KClass<*>.tags(): Set<Tag> {
   val annotation = annotation<io.kotest.core.annotation.Tags>() ?: return emptySet()
   return annotation.values.map { NamedTag(it) }.toSet()
}

fun Spec.resolvedThreads() = this.threads() ?: this.threads ?: 1

/**
 * Returns all spec level tags associated with this spec instance.
 */
fun Spec.resolvedTags(): Set<Tag> = this::class.tags() + this.tags() // TODO
