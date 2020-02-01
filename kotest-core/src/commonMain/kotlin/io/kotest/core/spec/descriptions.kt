package io.kotest.core.spec

import io.kotest.core.test.Description
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Returns a [Description] that can be used for a spec.
 *
 * If the spec has been annotated with @DisplayName (on supported platforms), then that will be used,
 * otherwise the default is to use the fully qualified class name.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name, so if @DisplayName is used, developers must ensure it does not
 * clash with another spec.
 */
fun KClass<*>.description(): Description {
   val name = this.annotation<DisplayName>()?.name ?: bestName()
   return Description.spec(name)
}


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)
