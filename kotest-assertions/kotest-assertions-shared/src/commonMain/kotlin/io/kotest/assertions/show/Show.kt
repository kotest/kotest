@file:Suppress("UNCHECKED_CAST")

package io.kotest.assertions.show

import kotlin.reflect.KClass

/**
 * The [Show] typeclass abstracts the ability to obtain a String representation of any object.
 * It is used as a replacement for Java's Object#toString so that custom representations of
 * the object can be printed.
 */
interface Show<in A> {

   /**
    * Returns a printable version of this object as an instance of [Printed]
    */
   fun show(a: A): Printed
}

/**
 * Represents a value that has been appropriately formatted for display in output logs or error messages.
 * For example, a null might be formatted as <null>.
 */
data class Printed(val value: String)

fun String.printed() = Printed(this)

fun Any?.show(): Printed = if (this == null) DefaultShow.show(this) else showFor(this).show(this)

fun <T : Any> showFor(t: T): Show<T> = platformShow(t) ?: commonShowFor(t)

expect fun <A : Any> platformShow(a: A): Show<A>?

@Suppress("UNCHECKED_CAST")
fun <T : Any> commonShowFor(t: T): Show<T> = when (t) {
   is String -> StringShow as Show<T>
   is Map<*, *> -> MapShow as Show<T>
   is BooleanArray -> ArrayShow
   is IntArray -> ArrayShow
   is ShortArray -> ArrayShow
   is FloatArray -> ArrayShow
   is DoubleArray -> ArrayShow
   is LongArray -> ArrayShow
   is ByteArray -> ArrayShow
   is CharArray -> ArrayShow
   is Array<*> -> ArrayShow
   is List<*> -> ListShow<T>() as Show<T>
   is Iterable<*> -> IterableShow<T>() as Show<T>
   is Long, t is Boolean, t is Int, t is Double, t is Float, t is Short, t is Byte -> DefaultShow
   is KClass<*> -> KClassShow as Show<T>
   else -> when {
      // this won't work in JS or native, so they'll get the boring old toString version
      io.kotest.mpp.reflection.isDataClass(t::class) -> dataClassShow<T>()
      else -> DefaultShow
   }
}

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   return when {
      root == node -> "(this ${root::class.simpleName})".printed()
      root is Iterable<*> && node is Iterable<*> ->
         if (root.toList() == node.toList()) "(this ${root::class.simpleName})".printed() else node.show()
      root is List<*> && node is Iterable<*> ->
         if (root == node.toList()) "(this ${root::class.simpleName})".printed() else node.show()
      else -> node.show()
   }
}
