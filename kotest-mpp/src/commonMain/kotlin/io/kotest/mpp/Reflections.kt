package io.kotest.mpp

import kotlin.reflect.KClass

expect val reflections: Reflections

interface Reflections {

   /**
    * Returns the fully qualified name for this class, or null
    */
   fun fqn(kclass: KClass<*>): String?

   /**
    * Returns the annotations on this class or empty list if not supported
    */
   fun annotations(kclass: KClass<*>): List<Annotation>

   /**
    * Returns true if this KClass is a data class.
    */
   fun isDataClass(kclass: KClass<*>): Boolean

   /**
    * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
    * it would return [a, b] and on unsupported platforms an empty list.
    */
   fun paramNames(fn: Function<*>): List<String>
}

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the simple name, or toString.
 */
fun KClass<*>.bestName(): String = reflections.fqn(this) ?: simpleName ?: this.toString()

/**
 * Finds the first annotation of type T on this class, or returns null if annotations
 * are not supported on this platform or the annotation is missing.
 */
inline fun <reified T> KClass<*>.annotation(): T? =
   reflections.annotations(this).filterIsInstance<T>().firstOrNull()

inline fun <reified T> KClass<*>.hasAnnotation(): Boolean =
   reflections.annotations(this).filterIsInstance<T>().isNotEmpty()

object BasicReflections : Reflections {
   override fun fqn(kclass: KClass<*>): String? = null
   override fun annotations(kclass: KClass<*>): List<Annotation> = emptyList()
   override fun isDataClass(kclass: KClass<*>): Boolean = false
   override fun paramNames(fn: Function<*>): List<String> = emptyList()
}
