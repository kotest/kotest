package io.kotest.mpp

import kotlin.reflect.KClass

expect val reflection: Reflection

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the simple name, or toString.
 */
fun KClass<*>.bestName(): String = reflection.fqn(this) ?: simpleName ?: this.toString()

/**
 * Finds the first annotation of type T on this class, or returns null if annotations
 * are not supported on this platform or the annotation is missing.
 */
inline fun <reified T> KClass<*>.annotation(): T? = reflection.annotations(this).filterIsInstance<T>().firstOrNull()

inline fun <reified T> KClass<*>.hasAnnotation(): Boolean = reflection.annotations(this).filterIsInstance<T>().isNotEmpty()

interface Reflection {

   /**
    * Returns the fully qualified name for the given class or null if the platform
    * does not expose this information.
    */
   fun fqn(kclass: KClass<*>): String?

   /**
    * Returns the annotations on this class or empty list if not supported
    */
   fun annotations(kclass: KClass<*>): List<Annotation>

   /**
    * Returns true if this class is a data class or false if it is not, or the platform does not
    * expose this information.
    */
   fun <T : Any> isDataClass(kclass: KClass<T>): Boolean

   /**
    * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
    * it would return [a, b] and on unsupported platforms an empty list.
    */
   fun paramNames(fn: Function<*>): List<String>?
}

object BasicReflection : Reflection {
   override fun fqn(kclass: KClass<*>): String? = null
   override fun annotations(kclass: KClass<*>): List<Annotation> = emptyList()
   override fun <T : Any> isDataClass(kclass: KClass<T>): Boolean = false
   override fun paramNames(fn: Function<*>): List<String>? = null
}
