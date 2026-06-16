package io.kotest.assertions.print

/**
 * Non-JVM implementation of [PrintContext] using a simple [MutableList].
 * This is safe because JS is single-threaded and Native has its own memory model.
 */
internal actual object PrintContext {
   private val visited = mutableListOf<Any>()

   actual fun isVisited(obj: Any): Boolean = visited.any { it === obj }

   actual fun push(obj: Any) {
      visited.add(obj)
   }

   actual fun pop() {
      visited.removeLastOrNull()
   }
}

