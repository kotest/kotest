package io.kotest.assertions.print

import kotlin.reflect.KClass

/**
 * Returns a [Print] for this non-null value by delegating to platform specific, or commonly
 * registered typeclasses. If neither matches, then the default [ToStringPrint] is returned.
 *
 * Platform specific [Print]s are returned first to allow for more optimized implementations
 * on those platforms.
 */
object PrintResolver {
   fun <A : Any> printFor(a: A): Print<A> = platformPrint(a) ?: commonPrintFor(a) ?: ToStringPrint
}

/**
 * Returns a [Print] typeclass for the value [a], if one exists in the platform specific [Print]s,
 * otherwise returns null.
 */
internal expect fun <A : Any> platformPrint(a: A): Print<A>?

/**
 * Returns a [Print] typeclass for the value [a] if one exists in the common or registered [Print]s,
 * otherwise returns null.
 */
@Suppress("UNCHECKED_CAST")
internal fun <A : Any> commonPrintFor(a: A): Print<A>? {
   val key: KClass<*>? = Printers.all().keys.firstOrNull { it.isInstance(a) }
   if (key != null) {
      val print: Print<*>? = Printers.all()[key]
      return print as Print<A>
   }
   return null
   // this won't work in JS or native, so they'll get the boring old toString version
//   if (io.kotest.mpp.reflection.isDataClass(a::class) && reflection.isPublic(a::class)) return dataClassPrint()
//   return null
}
