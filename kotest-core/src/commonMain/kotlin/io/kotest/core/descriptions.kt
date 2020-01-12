package io.kotest.core

import io.kotest.fp.getOrElse
import kotlin.reflect.KClass

/**
 * Returns a [Description] that can be used for a spec.
 *
 * If the spec has been annotated with @DisplayName (JVM only), then that will be used, otherwise
 * the default is to use the fully qualified class name.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name, so if @DisplayName is set, users must ensure it does not clash
 * with another.
 */
fun KClass<*>.description(): Description {
   val name = this.displayName().getOrElse(bestName())
   return Description.spec(name)
}
