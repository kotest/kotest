package io.kotest.engine.spec

import io.kotest.core.Tag
import io.kotest.core.spec.Spec
import io.kotest.engine.tags.tags


fun Spec.resolvedThreads() = this.threads() ?: this.threads ?: 1

/**
 * Returns all spec level tags associated with this spec instance.
 */
fun Spec.resolvedTags(): Set<Tag> = this::class.tags() + this.tags() // TODO
