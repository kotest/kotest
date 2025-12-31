package io.kotest.assertions.eq

/**
 * @param strictNumberEq used by number types to determine if they should be compared using == or by converting to the larger type.
 */
class EqContext(val strictNumberEq: Boolean) {

   private val visited = mutableListOf<Pair<Any?, Any?>>()

   companion object {
      const val MAX_DEPTH = 64
   }

   fun push(actual: Any?, expected: Any?) {
      if (visited.size >= MAX_DEPTH) {
         throw AssertionError("Cannot recursively match structures more than $MAX_DEPTH levels deep")
      }
      visited.add(actual to expected)
   }

   fun pop() = visited.removeLastOrNull()

   fun isVisited(actual: Any?, expected: Any?) = visited.any { (a, e) ->
      (a === actual && e === expected) || (a === expected && e === actual)
   }
}
