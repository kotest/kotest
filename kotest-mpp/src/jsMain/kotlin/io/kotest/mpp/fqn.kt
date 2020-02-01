package io.kotest.mpp

import kotlin.reflect.KClass

// qualified name is not supported on JS
actual fun KClass<*>.fqn(): String? = null

/**
 * Returns the annotations on this class or empty list if not supported
 */
actual fun KClass<*>.annotations(): List<Annotation> = emptyList()

// not supported on JS
actual val <T : Any> KClass<T>.isDataClass: Boolean?
   get() = null
