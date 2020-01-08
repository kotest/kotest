package io.kotest.core

import io.kotest.fp.Option
import io.kotest.fp.firstOption
import io.kotest.fp.some
import kotlin.reflect.KClass

/**
 * Returns the fully qualified name for this class, or none.
 */
actual fun KClass<*>.fqn(): Option<String> = this.java.canonicalName.some()

actual fun KClass<*>.displayName(): Option<String> {
   return annotations.filterIsInstance<DisplayName>().firstOption().map { it.name }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)
