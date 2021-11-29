@file:Suppress("UNCHECKED_CAST")

package io.kotest.assertions.print

import kotlin.reflect.KClass

/**
 * The [Print] typeclass abstracts the ability to obtain a
 * String representation of any object.
 *
 * It is used as a replacement for Java's Object#toString so
 * that custom representations of objects can be printed, in
 * addition to more visual representations of whitespace, nulls,
 * and so on.
 */
interface Print<in A> {
   fun print(a: A): Printed
}

/**
 * Represents a value that has been appropriately formatted
 * for display in assertion error messages.
 *
 * For example, a null might be formatted as <null>, whitespace
 * may be escaped and the empty string may be quoted.
 */
data class Printed(val value: String)

/**
 * Wraps the given string in a [Printed].
 */
fun String.printed() = Printed(this)

/**
 * Obtains a [Printed] instance for the given receiver by delegating to the common
 * and platform print lookups.
 */
fun Any?.print(): Printed = if (this == null) NullPrint.print(this) else printFor(this).print(this)

/**
 * Returns a [Print] for this non-null value by delegating to platform specific, or commonly
 * registered typeclasses. If neither matches, then the default [ToStringPrint] is returned.
 */
fun <A : Any> printFor(a: A): Print<A> = platformPrint(a) ?: commonPrintFor(a) ?: ToStringPrint

/**
 * Returns a [Print] instance if one exists in the platform specific [Print]s.
 */
expect fun <A : Any> platformPrint(a: A): Print<A>?

/**
 * Returns a [Print] instance if one exists in the common or registered [Print]s.
 */
fun <A : Any> commonPrintFor(a: A): Print<A>? {
   val kclass: KClass<*>? = Printers.all().keys.firstOrNull { it.isInstance(a) }
   if (kclass != null) {
      val print: Print<*>? = Printers.all()[kclass]
      return print as Print<A>
   }
   // this won't work in JS or native, so they'll get the boring old toString version
   if (io.kotest.mpp.reflection.isDataClass(a::class)) return dataClassPrint()
   return null
}

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   return when {
      root == node -> "(this ${root::class.simpleName})".printed()
      root is Iterable<*> && node is Iterable<*> ->
         if (root.toList() == node.toList()) "(this ${root::class.simpleName})".printed() else node.print()
      root is List<*> && node is Iterable<*> ->
         if (root == node.toList()) "(this ${root::class.simpleName})".printed() else node.print()
      else -> node.print()
   }
}
