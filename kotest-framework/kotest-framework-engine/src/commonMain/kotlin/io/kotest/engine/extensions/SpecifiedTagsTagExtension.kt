package io.kotest.engine.extensions

import io.kotest.engine.tags.TagExpression
import io.kotest.core.extensions.TagExtension

/**
 * A [TagExtension] that will provide the [TagExpression] given to the constructor.
 */
internal class SpecifiedTagsTagExtension(private val tags: TagExpression) : TagExtension {
   override fun tags(): TagExpression = tags
}
