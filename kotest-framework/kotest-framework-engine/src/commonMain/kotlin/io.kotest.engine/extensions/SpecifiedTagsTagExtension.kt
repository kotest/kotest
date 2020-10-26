package io.kotest.engine.extensions

import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension

/**
 * Tag extension which will return tags specified in the constructor.
 */
class SpecifiedTagsTagExtension(private val tags: Tags) : TagExtension {
   override fun tags(): Tags = tags
}
