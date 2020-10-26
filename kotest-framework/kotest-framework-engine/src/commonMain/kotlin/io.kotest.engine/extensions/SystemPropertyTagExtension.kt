package io.kotest.engine.extensions

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension
import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.fp.getOrElse
import io.kotest.fp.toOption
import io.kotest.mpp.sysprop

/**
 * This [TagExtension] includes and excludes tags using the system properties:
 * [KotestEngineSystemProperties.tagExpression], [KotestEngineSystemProperties.includeTags]
 * and [KotestEngineSystemProperties.excludeTags].
 *
 * Note: If [KotestEngineSystemProperties.tagExpression] is used then the other two properties will be ignored.
 *
 * On non-JVM targets this extension will have no effect.
 */
object SystemPropertyTagExtension : TagExtension {

   override fun tags(): Tags {

      fun readTagsProperty(name: String): List<Tag> =
         sysprop(name).toOption().getOrElse("").split(',').filter { it.isNotBlank() }.map {
            NamedTag(it.trim())
         }

      val includedTags = readTagsProperty(KotestEngineSystemProperties.includeTags)
      val excludedTags = readTagsProperty(KotestEngineSystemProperties.excludeTags)
      val expression = sysprop(KotestEngineSystemProperties.tagExpression)

      return if (expression == null) Tags(includedTags.toSet(), excludedTags.toSet()) else Tags(expression)
   }
}
