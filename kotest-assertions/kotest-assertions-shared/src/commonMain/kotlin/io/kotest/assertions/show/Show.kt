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
 * Global object that allows for registration of custom [Show] typeclasses.
 */
object Shows {

   private val shows = mutableMapOf<KClass<*>, Show<*>>().apply {
      put(String::class, StringShow)
      put(Map::class, MapShow)
      put(BooleanArray::class, ArrayShow)
      put(IntArray::class, ArrayShow)
      put(ShortArray::class, ArrayShow)
      put(FloatArray::class, ArrayShow)
      put(DoubleArray::class, ArrayShow)
      put(LongArray::class, ArrayShow)
      put(ByteArray::class, ArrayShow)
      put(CharArray::class, ArrayShow)
      put(Array::class, ArrayShow)
      put(List::class, ListShow<Any>())
      put(Iterable::class, IterableShow<Any>())
      put(Long::class, DefaultShow)
      put(Int::class, DefaultShow)
      put(Short::class, DefaultShow)
      put(Byte::class, DefaultShow)
      put(Double::class, DefaultShow)
      put(Float::class, DefaultShow)
      put(Boolean::class, DefaultShow)
      put(KClass::class, KClassShow)
   }

   fun <T : Any> add(kclass: KClass<out T>, show: Show<T>) {
      shows[kclass] = show
   }

   fun remove(kclass: KClass<*>) {
      shows.remove(kclass)
   }

   fun all() = shows.toMap()
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
fun <T : Any> commonShowFor(t: T): Show<T> {
   // lookup a show from the registered typeclasses
   val kclass = Shows.all().keys.firstOrNull { it.isInstance(t) }
   if (kclass != null) Shows.all()[kclass] as Show<T>
   // this won't work in JS or native, so they'll get the boring old toString version
   if (io.kotest.mpp.reflection.isDataClass(t::class)) return dataClassShow<T>()
   return DefaultShow
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
