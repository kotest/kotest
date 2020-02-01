package io.kotest.core.spec

import io.kotest.assertions.bestName
import io.kotest.core.displayName
import io.kotest.core.test.Description
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
   val name = this.displayName() ?: bestName()
   return Description.spec(name)
}
