package io.kotest.engine.extensions

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import io.kotest.core.internal.tags.isPotentiallyActive
import io.kotest.core.internal.tags.parse
import io.kotest.core.internal.tags.activeTags
import kotlin.reflect.KClass

/**
 * Filters any [Spec] that can be eagerly excluded based on the @[Tags] annotation at the class level.
 */
object TagsExcludedDiscoveryExtension : DiscoveryExtension {

   fun afterScan(classes: List<KClass<out Spec>>, tags: Tags): List<KClass<out Spec>> {
      return classes.filter { tags.parse().isPotentiallyActive(it) }
   }

   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return afterScan(classes, configuration.activeTags())
   }
}
