package io.kotest.core.extensions

import io.kotest.core.Tags
import io.kotest.core.config.Project
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Filters Specs that can be excluded based on the @Tags annotation at the Spec Level.
 */
object TagsExcludedDiscoveryExtension : DiscoveryExtension {

   fun afterScan(classes: List<KClass<out Spec>>, tags: Tags): List<KClass<out Spec>> {
      return classes.filter { tags.isPotentiallyActive(it) }
   }

   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return afterScan(classes, Project.tags())
   }
}
