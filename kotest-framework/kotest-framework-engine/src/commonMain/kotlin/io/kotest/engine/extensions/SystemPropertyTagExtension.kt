package io.kotest.engine.extensions

import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.engine.tags.TagExpression
import io.kotest.core.extensions.TagExtension
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.common.syspropOrEnv

/**
 * This [TagExtension] includes and excludes tags using the system properties:
 * [KotestEngineProperties.TAG_EXPRESSION], [KotestEngineProperties.includeTags]
 * and [KotestEngineProperties.excludeTags].
 *
 * Note: If [KotestEngineProperties.TAG_EXPRESSION] is used then the other two properties will be ignored.
 *
 * On non-JVM targets this extension will have no effect.
 */
object SystemPropertyTagExtension : TagExtension {

   override fun tags(): TagExpression {

      fun readTagsProperty(name: String): List<Tag> =
         (syspropOrEnv(name) ?: "").split(',').filter { it.isNotBlank() }.map { NamedTag(it.trim()) }

      val includedTags = readTagsProperty(KotestEngineProperties.includeTags)
      val excludedTags = readTagsProperty(KotestEngineProperties.excludeTags)
      val expression = syspropOrEnv(KotestEngineProperties.TAG_EXPRESSION)

      return if (expression == null)
         TagExpression(includedTags.toSet(), excludedTags.toSet())
      else
         TagExpression(expression)
   }
}
