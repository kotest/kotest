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

fun <T : Any> showFor(t: T): Show<T> = when (t) {
   is String -> StringShow as Show<T>
   is Map<*, *> -> MapShow as Show<T>
   is Long, t is Boolean, t is Int, t is Double, t is Float, t is Short, t is Byte -> DefaultShow
   is KClass<*> -> KClassShow as Show<T>
   else -> when {
      // this won't work in JS or native, so they'll get the boring old toString version
      t::class.isDataClass ?: false -> dataClassShow<T>()
      else -> DefaultShow
   }
}
