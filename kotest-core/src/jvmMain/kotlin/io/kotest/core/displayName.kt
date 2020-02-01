package io.kotest.core

import kotlin.reflect.KClass

actual fun KClass<*>.displayName(): String? {
   return annotations.filterIsInstance<DisplayName>().firstOrNull()?.name
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DisplayName(val name: String)
