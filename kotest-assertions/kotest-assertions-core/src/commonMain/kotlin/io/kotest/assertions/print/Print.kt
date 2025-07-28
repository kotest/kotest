@file:Suppress("UNCHECKED_CAST")

package io.kotest.assertions.print

/**
 * The [Print] typeclass abstracts the ability to obtain a
 * [String] representation of any object.
 *
 * It is used as a replacement for Java's Object#toString so
 * that custom representations of objects can be printed, in
 * addition to more visual representations of whitespace, nulls,
 * and so on.
 */
fun interface Print<in A> {

   /**
    * Returns a [Printed] for the given instance [a].
    */
   fun print(a: A): Printed
}

internal fun indent(level: Int): String = "  ".repeat(level)

/**
 * Obtains a [Printed] instance for the given receiver by delegating to the common
 * and platform print lookups.
 */
@Deprecated("Use PrintResolver.printFor instead", ReplaceWith("PrintResolver.printFor(this).print(this)"))
fun Any?.print(): Printed = if (this == null) NullPrint.print(this) else PrintResolver.printFor(this).print(this)

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   return when (root) {
      node -> Printed("(this ${root::class.simpleName})")
      is Iterable<*> if node is Iterable<*> && root.toList() == node.toList() -> Printed("(this ${root::class.simpleName})")
      is Iterable<*> if node is Iterable<*> -> node.print()
      is List<*> if node is Iterable<*> && root == node.toList() -> Printed("(this ${root::class.simpleName})")
      is List<*> if node is Iterable<*> -> node.print()
      else -> node.print()
   }
}
