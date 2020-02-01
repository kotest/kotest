package io.kotest.mpp

import kotlin.reflect.KClass

actual fun KClass<*>.fqn(): String? = this.qualifiedName
/**
 * Returns the annotations on this class or empty list if not supported
 */
actual fun KClass<*>.annotations(): List<Annotation> = this.annotations

/**
 * Returns true if this KClass is a data class.
 */
actual val <T : Any> KClass<T>.isDataClass: Boolean?
   get() = this.isData
