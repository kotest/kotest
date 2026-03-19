package io.kotest.engine.extensions.tags

import io.kotest.common.env
import io.kotest.common.sysprop
import io.kotest.core.NamedTag
import io.kotest.core.Tag
import io.kotest.core.extensions.TagExtension
import io.kotest.engine.config.KotestEngineEnvVars
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.engine.tags.TagExpression

/**
 * This [io.kotest.core.extensions.TagExtension] includes and excludes tags using the system properties:
 * [io.kotest.engine.config.KotestEngineProperties.TAG_EXPRESSION], [io.kotest.engine.config.KotestEngineProperties.INCLUDE_TAGS]
 * and [io.kotest.engine.config.KotestEngineProperties.EXCLUDE_TAGS] or the equivalent environment variables.
 *
 * Note: If [io.kotest.engine.config.KotestEngineProperties.TAG_EXPRESSION] is used then the other two properties will be ignored.
 *
 * This extension is registered automatically by the Kotest engine.
 *
 * On non-JVM targets system properties have no effect, so you must use the environment variables.
 */
object SystemPropertyOrEnvTagExtension : TagExtension {

   override fun tags(): TagExpression {

      val includedTags = readTags(KotestEngineProperties.INCLUDE_TAGS)
      val excludedTags = readTags(KotestEngineProperties.EXCLUDE_TAGS)
      val expression = sysprop(KotestEngineProperties.TAG_EXPRESSION) ?: env(KotestEngineEnvVars.TAG_EXPRESSION)

      return if (expression == null)
         TagExpression.Companion(includedTags.toSet(), excludedTags.toSet())
      else
         TagExpression(expression)
   }

   private fun readTags(prop: String): List<Tag> =
      (sysprop(prop) ?: "").split(',')
         .filter { it.isNotBlank() }
         .map { NamedTag(it.trim()) }
}
