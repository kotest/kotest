package io.kotest.core.extensions

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.Spec
import io.kotest.mpp.hasAnnotation
import kotlin.reflect.KClass

// any spec that is annotated with @Ignored is filtered out
object IgnoredSpecDiscoveryExtension : DiscoveryExtension {
   override fun afterScan(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return classes.filterNot { it.hasAnnotation<Ignored>() }
   }
}
