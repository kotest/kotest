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
 */
private object PrintContext {
   private val visited = mutableListOf<Any>()

   fun isVisited(obj: Any): Boolean = visited.any { it === obj }

   fun push(obj: Any) {
      visited.add(obj)
   }

   fun pop() {
      visited.removeLastOrNull()
   }
}

internal fun recursiveRepr(root: Any, node: Any?): Printed {
   return when (root) {
      node -> Printed("(this ${root::class.simpleName})")
      is Iterable<*> if node is Iterable<*> && root.toList() == node.toList() -> Printed("(this ${root::class.simpleName})")
      is Iterable<*> if node is Iterable<*> -> printWithCycleDetection(node)
      is List<*> if node is Iterable<*> && root == node.toList() -> Printed("(this ${root::class.simpleName})")
      is List<*> if node is Iterable<*> -> printWithCycleDetection(node)
      is Map<*, *> if node is Map<*, *> -> printWithCycleDetection(node)
      else -> node.print()
   }
}

/**
 * Prints a value with cycle detection. If the object has already been visited
 * during the current print operation, returns a cycle indicator instead of
 * recursively printing to avoid StackOverflowError.
 */
private fun printWithCycleDetection(node: Any?): Printed {
   if (node == null) return NullPrint.print(null)

   return if (PrintContext.isVisited(node)) {
      Printed("(this ${node::class.simpleName})")
   } else {
      PrintContext.push(node)
      try {
         node.print()
      } finally {
         PrintContext.pop()
      }
   }
}


