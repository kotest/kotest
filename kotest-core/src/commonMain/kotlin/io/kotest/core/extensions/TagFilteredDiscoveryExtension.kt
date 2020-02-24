package io.kotest.core.extensions

import io.kotest.core.StringTag
import io.kotest.core.annotation.Tags
import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Allows include / exclude [Spec] with [Tags] annotation.
 */
object TagFilteredDiscoveryExtension: DiscoveryExtension {
   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return classes.filter { cls ->
         val annotation = cls.annotations.firstOrNull { it is Tags } as Tags?
         annotation?.let { annotation ->
            val tags = annotation.values.map { value -> StringTag(value) }.toSet()
            Project.tags().isActive(tags)
         } ?: true
      }
   }
}
