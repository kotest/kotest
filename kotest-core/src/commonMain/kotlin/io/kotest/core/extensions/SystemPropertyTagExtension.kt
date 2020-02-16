package io.kotest.core.extensions

import io.kotest.core.StringTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.fp.getOrElse
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

/**
 * This [TagExtension] includes and excludes tags using the system properties
 * 'kotest.tags.include' and 'kotest.tags.exclude'.
 *
 * On non-JVM targets this extension will have no effect.
 */
object SystemPropertyTagExtension : TagExtension {

   override fun tags(): Tags {

      fun readTagsProperty(name: String): List<Tag> =
         sysprop(name).toOption().getOrElse("").split(',').filter { it.isNotBlank() }.map {
            StringTag(
               it.trim()
            )
         }

      val includedTags = readTagsProperty("kotest.tags.include")
      val excludedTags = readTagsProperty("kotest.tags.exclude")

      return Tags(includedTags.toSet(), excludedTags.toSet())
   }
}
