package io.kotest.assertions.print

/**
 * JVM implementation of [PrintContext] using [ThreadLocal] for thread-safety.
 * Each thread gets its own visited list, preventing [ConcurrentModificationException]
 * when printing from multiple coroutines or threads concurrently.
 */
internal actual object PrintContext {
   private val visited = ThreadLocal.withInitial { mutableListOf<Any>() }

   actual fun isVisited(obj: Any): Boolean = visited.get().any { it === obj }

   actual fun push(obj: Any) {
      visited.get().add(obj)
   }

   actual fun pop() {
      visited.get().removeLastOrNull()
   }
}
