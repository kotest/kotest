package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.jvm.reflect

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

/**
 * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
 * it would return [a, b] and on unsupported platforms an empty list.
 */
actual val Function<*>.paramNames: List<String>
   get() = reflect()?.parameters?.mapNotNull { it.name } ?: emptyList()
