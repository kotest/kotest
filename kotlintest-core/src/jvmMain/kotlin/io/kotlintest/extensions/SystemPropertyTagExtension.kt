package io.kotlintest.extensions

import io.kotlintest.StringTag
import io.kotlintest.Tag
import io.kotlintest.Tags

/**
 * This [TagExtension] includes and excludes tags using the system properties
 * 'kotlintest.tags.include' and 'kotlintest.tags.exclude'
 */
object SystemPropertyTagExtension : TagExtension {

  override fun tags(): Tags {

    fun readTagsProperty(name: String): List<Tag> =
      (System.getProperty(name) ?: "").split(',').filter { it.isNotBlank() }.map { StringTag(it.trim()) }

    val includedTags = readTagsProperty("kotlintest.tags.include")
    val excludedTags = readTagsProperty("kotlintest.tags.exclude")

    return Tags(includedTags.toSet(), excludedTags.toSet())
  }
}
