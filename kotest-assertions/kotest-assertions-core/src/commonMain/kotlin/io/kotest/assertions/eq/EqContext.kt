package io.kotest.assertions.eq

class EqContext {
   private val visited = mutableListOf<Pair<Any?, Any?>>()

   companion object {
      const val MAX_DEPTH = 64
   }

   fun push(actual: Any?, expected: Any?) {
      if (visited.size >= MAX_DEPTH) {
         throw AssertionError("Max recursion depth ($MAX_DEPTH) reached during equality check")
      }
      visited.add(actual to expected)
   }

   fun pop() = visited.removeLastOrNull()

   fun isVisited(actual: Any?, expected: Any?) = visited.any { (a, e) ->
      (a === actual && e === expected) || (a === expected && e === actual)
   }
}
