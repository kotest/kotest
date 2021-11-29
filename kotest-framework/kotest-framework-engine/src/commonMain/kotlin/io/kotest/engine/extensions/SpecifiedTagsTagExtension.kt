package io.kotest.engine.extensions

import io.kotest.core.TagExpression
import io.kotest.core.extensions.TagExtension

/**
 * A [TagExtension] that will provide the [TagExpression] given to the constructor.
 */
class SpecifiedTagsTagExtension(private val tags: TagExpression) : TagExtension {
   override fun tags(): TagExpression = tags
}
