package io.kotest.engine.extensions

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.env
import io.kotest.mpp.sysprop

/**
 * This [TagExtension] includes and excludes tags using the system properties:
 * [KotestEngineProperties.tagExpression], [KotestEngineProperties.includeTags]
 * and [KotestEngineProperties.excludeTags].
 *
 * Note: If [KotestEngineProperties.tagExpression] is used then the other two properties will be ignored.
 *
 * On non-JVM targets this extension will have no effect.
 */
object SystemPropertyTagExtension : TagExtension {

   override fun tags(): Tags {

      fun readTagsProperty(name: String): List<Tag> =
         (sysprop(name) ?: env(name) ?: "").split(',').filter { it.isNotBlank() }.map { NamedTag(it.trim()) }

      val includedTags = readTagsProperty(KotestEngineProperties.includeTags)
      val excludedTags = readTagsProperty(KotestEngineProperties.excludeTags)
      val expression = sysprop(KotestEngineProperties.tagExpression) ?: env(KotestEngineProperties.tagExpression)

      return if (expression == null) Tags(includedTags.toSet(), excludedTags.toSet()) else Tags(expression)
   }
}
