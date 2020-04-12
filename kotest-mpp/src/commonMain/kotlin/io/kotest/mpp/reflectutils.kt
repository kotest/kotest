package io.kotest.mpp

import kotlin.reflect.KClass

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the simple name, or toString.
 */
fun KClass<*>.bestName(): String = fqn() ?: simpleName ?: this.toString()

/**
 * Returns the fully qualified name for this class, or null
 */
expect fun KClass<*>.fqn(): String?

/**
 * Returns the annotations on this class or empty list if not supported
 */
expect fun KClass<*>.annotations(): List<Annotation>

/**
 * Finds the first annotation of type T on this class, or returns null if annotations
 * are not supported on this platform or the annotation is missing.
 */
inline fun <reified T> KClass<*>.annotation(): T? = annotations().filterIsInstance<T>().firstOrNull()

inline fun <reified T> KClass<*>.hasAnnotation(): Boolean = annotations().filterIsInstance<T>().isNotEmpty()

/**
 * Returns true if this KClass is a data class, false if it is not, or null if the functionality
 * is not supported on the platform.
 */
expect val <T : Any> KClass<T>.isDataClass: Boolean?

/**
 * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
 * it would return [a, b] and on unsupported platforms an empty list.
 */
expect val Function<*>.paramNames: List<String>
