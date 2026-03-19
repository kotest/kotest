package io.kotest.engine.extensions.tags

import io.kotest.core.extensions.TagExtension
import io.kotest.engine.tags.TagExpression

/**
 * A [io.kotest.core.extensions.TagExtension] that will provide the [io.kotest.engine.tags.TagExpression] given to the constructor.
 */
internal class SpecifiedTagsTagExtension(private val tags: TagExpression) : TagExtension {
   override fun tags(): TagExpression = tags
}
