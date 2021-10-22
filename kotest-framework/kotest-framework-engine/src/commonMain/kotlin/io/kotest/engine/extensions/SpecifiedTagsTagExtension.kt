package io.kotest.engine.extensions

import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension

/**
 * A [TagExtension] that will provide the [Tags] given to the constructor.
 */
class SpecifiedTagsTagExtension(private val tags: Tags) : TagExtension {
   override fun tags(): Tags = tags
}
