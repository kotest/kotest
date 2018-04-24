package io.kotlintest.extensions

import io.kotlintest.StringTag
import io.kotlintest.Tag
import io.kotlintest.Tags

/**
 * Returns [Tags] to be used by the test runner.
 *
 * A [Tag] can be added to any test and then specific tags can be included
 * or excluded via [TagExtension] instances, which will cause tests that do not
 * match to be skipped.
 *
 * Note: If multiple extensions are registered then all returned
 * [Tags] are combined together.
 *
 * The default [SystemPropertyTagExtension] is automatically registered.
 */
interface TagExtension : ProjectLevelExtension {
  fun tags(): Tags = Tags.Empty
}

object SystemPropertyTagExtension : TagExtension {

  override fun tags(): Tags {

    fun readTagsProperty(name: String): List<Tag> =
        (System.getProperty(name) ?: "").split(',').filter { it.isNotBlank() }.map { StringTag(it.trim()) }

    val includedTags = readTagsProperty("kotlintest.tags.include")
    val excludedTags = readTagsProperty("kotlintest.tags.exclude")

    return Tags(includedTags.toSet(), excludedTags.toSet())
  }
}