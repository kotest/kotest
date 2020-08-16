package io.kotest.engine.spec

import io.kotest.core.Tag
import io.kotest.core.config.configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.engine.tags.tags

fun Spec.resolvedExtensions(): List<Extension> {
   return this.extensions() + this.registeredExtensions() + configuration.extensions()
}

fun Spec.resolvedThreads() = this.threads() ?: this.threads ?: 1

/**
 * Returns all spec level tags associated with this spec instance.
 */
fun Spec.resolvedTags(): Set<Tag> = this::class.tags() + this.tags() // TODO
