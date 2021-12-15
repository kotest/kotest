package io.kotest.mpp

import kotlin.reflect.KClass
import kotlin.reflect.KType

expect val reflection: Reflection

/**
 * Groups together some basic platform agnostic reflection oeprations.
 */
interface Reflection {

   /**
    * Returns the fully qualified name for the given class or null if the platform
    * does not expose this information.
    */
   fun fqn(kclass: KClass<*>): String?

   /**
    * Returns the annotations on this class or empty list if not supported.
    *
    * @param recursive if true will recursively gather annotations on annotations...
    */
   fun annotations(kclass: KClass<*>, recursive: Boolean): List<Annotation>

   /**
    * Returns true if this class is a data class or false if it is not, or the platform does not
    * expose this information.
    */
   fun <T : Any> isDataClass(kclass: KClass<T>): Boolean

   fun isPublic(kclass: KClass<out Any>): Boolean

   /**
    * Returns the names of the parameters if supported. Eg, for `fun foo(a: String, b: Boolean)` on the JVM
    * it would return a, b and on unsupported platforms an empty list.
    */
   fun paramNames(fn: Function<*>): List<String>?

   /**
    * Returns a list of the class member properties defined in the primary constructor, if supported on
    * the platform, otherwise returns an empty list.
    */
   fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property>

   /**
    * Returns a new instance created from the no arg constructor, if supported
    */
   fun <T : Any> newInstanceNoArgConstructor(klass: KClass<T>): T

   fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(klass: KClass<T>): T

   fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean
}

object BasicReflection : Reflection {
   override fun fqn(kclass: KClass<*>): String? = null
   override fun annotations(kclass: KClass<*>, recursive: Boolean): List<Annotation> = emptyList()
   override fun <T : Any> isDataClass(kclass: KClass<T>): Boolean = false
   override fun <T : Any> isEnumClass(kclass: KClass<T>): Boolean = false
   override fun isPublic(kclass: KClass<out Any>): Boolean = false
   override fun paramNames(fn: Function<*>): List<String>? = null
   override fun <T : Any> primaryConstructorMembers(klass: KClass<T>): List<Property> = emptyList()
   override fun <T : Any> newInstanceNoArgConstructor(klass: KClass<T>): T = error("Unsupported on this platform")
   override fun <T : Any> newInstanceNoArgConstructorOrObjectInstance(klass: KClass<T>): T =
      error("Unsupported on this platform")
}

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the simple name, or toString.
 */
fun KClass<*>.bestName(): String = reflection.fqn(this) ?: simpleName ?: this.toString()

fun KClass<*>.qualifiedNameOrNull(): String? = reflection.fqn(this)

/**
 * Finds the first annotation of type T on this class, or returns null if annotations
 * are not supported on this platform or the annotation is missing.
 *
 * This method will recursively included composed annotations.
 */
inline fun <reified T : Any> KClass<*>.annotation(): T? {
   return reflection.annotations(this, true).filterIsInstance<T>().firstOrNull()
}

/**
 * Finds all annotations of type T on this class, or returns an empty list if annotations
 * are not supported on this platform or the annotation is not present.
 *
 * This method will recursively included composed annotations.
 */
inline fun <reified T : Any> KClass<*>.annotations(): List<T> {
   return reflection.annotations(this, true).filterIsInstance<T>()
}

/**
 * Returns true if this class has the given annotation.
 *
 * This will recursively check for annotations by looking for annotations on annotations.
 */
inline fun <reified T : Any> KClass<*>.hasAnnotation(): Boolean = this.annotation<T>() != null

/**
 * Returns an instance of this KClass via a no-arg default constructor or if this kclass
 * is an object, will return the object instance.
 *
 * Note: JVM only
 */
fun <T : Any> KClass<T>.newInstanceNoArgConstructorOrObjectInstance(): T =
   reflection.newInstanceNoArgConstructorOrObjectInstance(this)

/**
 * Returns an instance of this KClass via a no-arg default constructor.
 * Will error if no such constructor exists.
 *
 * Note: JVM only
 */
fun <T : Any> KClass<T>.newInstanceNoArgConstructor(): T = reflection.newInstanceNoArgConstructor(this)

data class Property(val name: String, val type: KType, val call: (Any) -> Any?)
