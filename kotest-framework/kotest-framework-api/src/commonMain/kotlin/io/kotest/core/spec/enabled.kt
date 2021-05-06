package io.kotest.core.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.test.Enabled
import io.kotest.mpp.annotation
import io.kotest.mpp.newInstanceNoArgConstructor
import kotlin.reflect.KClass

/**
 * Determines if a spec is enabled based on annotations and tags.
 * Annotations are only supported on the JVM.
 */
@PublishedApi
internal fun isSpecEnabled(kclass: KClass<out Spec>): Enabled {

   val enabledIf = kclass.annotation<EnabledIf>()
   if (enabledIf != null) {
      val condition = enabledIf.enabledIf.newInstanceNoArgConstructor()
      if (!condition.enabled(kclass)) return Enabled.disabled
   }

   val ignored = kclass.annotation<Ignored>()
   if (ignored != null) return Enabled.disabled

   return Enabled.enabled
}
