package io.kotest.core

import io.kotest.core.spec.SpecConfiguration
import kotlin.reflect.KClass

/**
 * A name / id for this spec class which is used as the parent name for tests.
 * By default this will return the fully qualified class name, unless the spec
 * class is annotated with @DisplayName (JVM only).
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name.
 */
fun KClass<out SpecConfiguration>.displayName(): String {
   return when (val displayName = annotations.find { it is DisplayName }) {
      is DisplayName -> displayName.name
      else -> java.canonicalName
   }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)
