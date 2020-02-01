package io.kotest.mpp

import kotlin.reflect.KClass

/**
 * Returns the fully qualified name for this class, or null
 */
actual fun KClass<*>.fqn(): String? = this.qualifiedName

/**
 * Returns the annotations on this class or empty list if not supported
 */
actual fun KClass<*>.annotations(): List<Annotation> = this.annotations()

actual val <T : Any> KClass<T>.isDataClass: Boolean?
   get() = null
