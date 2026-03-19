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

/**
 * Obtains a [Printed] instance for the given receiver by delegating to the common
 * and platform print lookups.
 */
fun Any?.print(): Printed = if (this == null) NullPrint.print(this) else PrintResolver.printFor(this).print(this)

/**
 * Context for tracking visited objects during recursive printing to detect cycles.
 * Uses reference equality (===) to detect reference cycles.
 *
 * Platform-specific implementations ensure thread-safety on JVM while remaining
 * efficient on single-threaded platforms like JS.
 */
internal expect object PrintContext {
   fun isVisited(obj: Any): Boolean
   fun push(obj: Any)
   fun pop()
}

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   // Use reference equality (===) to detect cycles without triggering equals() which could recurse
   return when {
      root === node -> Printed("(this ${root::class.simpleName})")
      node == null -> NullPrint.print(null)
      PrintContext.isVisited(node) -> Printed("(this ${root::class.simpleName})")
      node is Iterable<*> || node is Map<*, *> -> printWithCycleDetection(node)
      else -> node.print()
   }
}

/**
 * Prints a value with cycle detection. If the object has already been visited
 * during the current print operation, returns a cycle indicator instead of
 * recursively printing to avoid StackOverflowError.
 */
private fun printWithCycleDetection(node: Any?): Printed {
   return when (node) {
      null -> NullPrint.print(null)
      PrintContext::isVisited -> Printed("(this ${node::class.simpleName})")
      else -> {
         PrintContext.push(node)
         runCatching {
            node.print()
         }.also {
            PrintContext.pop()
         }.getOrThrow()
      }
   }
}


