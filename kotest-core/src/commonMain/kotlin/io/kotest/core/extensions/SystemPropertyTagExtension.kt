package io.kotest.core.extensions

import io.kotest.core.StringTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.engine.KotestFrameworkSystemProperties
import io.kotest.fp.getOrElse
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

/**
 * This [TagExtension] includes and excludes tags using the system properties:
 * 'kotest.tags', 'kotest.tags.include' and 'kotest.tags.exclude'.
 *
 * Note: If 'kotest.tags' is used then the other two properties will be ignored.
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

      val includedTags = readTagsProperty(KotestFrameworkSystemProperties.includeTags)
      val excludedTags = readTagsProperty(KotestFrameworkSystemProperties.excludeTags)
      val expression = sysprop(KotestFrameworkSystemProperties.tagExpression)

      return if (expression == null) Tags(includedTags.toSet(), excludedTags.toSet()) else Tags(expression)
   }
}
