package io.kotest.engine.extensions

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import io.kotest.mpp.newInstanceNoArgConstructor
import kotlin.reflect.KClass

/**
 * Filters out any [Spec] annotated with which @[EnabledIf] evaluates to false.
 */
object EnabledConditionSpecDiscoveryExtension : DiscoveryExtension {
   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return classes
         .filterNot { !it.isEnabled() }
   }

   private fun <T : Spec> KClass<T>.isEnabled(): Boolean {
      return annotation<EnabledIf>()?.enabledIf?.newInstanceNoArgConstructor()?.enabled(this) ?: true
   }
}
