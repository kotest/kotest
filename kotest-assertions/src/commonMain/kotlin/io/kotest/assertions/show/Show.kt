@file:Suppress("UNCHECKED_CAST")

package io.kotest.assertions.show

import io.kotest.mpp.isDataClass
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

data class Printed(val value: String)

fun String.printed() = Printed(this)

fun Any?.show(): Printed = if (this == null) DefaultShow.show(this) else showFor(this).show(this)

fun <T : Any> showFor(t: T): Show<T> = platformShow(t) ?: commonShowFor(t)

expect fun <A : Any> platformShow(a: A): Show<A>?

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
      t::class.isDataClass ?: false -> dataClassShow<T>()
      else -> DefaultShow
   }
}

object ArrayShow : Show<Any> {
   override fun show(a: Any): Printed = when (a) {
      is LongArray -> ListShow<Long>().show(a.asList())
      is IntArray -> ListShow<Int>().show(a.asList())
      is ShortArray -> ListShow<Short>().show(a.asList())
      is ByteArray -> ListShow<Byte>().show(a.asList())
      is DoubleArray -> ListShow<Double>().show(a.asList())
      is FloatArray -> ListShow<Float>().show(a.asList())
      is BooleanArray -> ListShow<Boolean>().show(a.asList())
      is CharArray -> ListShow<Char>().show(a.asList())
      is Array<*> -> ListShow<Any>().show(a.asList() as List<Any>)
      else -> throw UnsupportedOperationException()
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

class IterableShow<T> : Show<Iterable<T>> {
   override fun show(a: Iterable<T>): Printed = ListShow<T>().show(a.toList())
}

class ListShow<T> : Show<List<T>> {

   private val maxCollectionSnippetSize = 20

   override fun show(a: List<T>): Printed {
      return if (a.isEmpty()) Printed("[]") else {
         val remainingItems = a.size - maxCollectionSnippetSize

         val suffix = when {
            remainingItems <= 0 -> "]"
            else -> "] and $remainingItems more"
         }

         return a.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = suffix,
            limit = maxCollectionSnippetSize
         ) {
            when {
               it is Iterable<*> && it.toList() == a && a.size == 1 -> a[0].toString()
               else -> recursiveRepr(a, it).value
            }
         }.printed()
      }
   }
}
