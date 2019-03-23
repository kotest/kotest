package io.kotlintest.extensions

import io.kotlintest.StringTag
import io.kotlintest.Tag
import io.kotlintest.Tags
import io.kotlintest.internal.systemProperty

object SystemPropertyTagExtension : TagExtension {

  override fun tags(): Tags {

    fun readTagsProperty(name: String): List<Tag> =
        (systemProperty(name) ?: "").split(',').filter { it.isNotBlank() }.map { StringTag(it.trim()) }

    val includedTags = readTagsProperty("kotlintest.tags.include")
    val excludedTags = readTagsProperty("kotlintest.tags.exclude")

    return Tags(includedTags.toSet(), excludedTags.toSet())
  }
}