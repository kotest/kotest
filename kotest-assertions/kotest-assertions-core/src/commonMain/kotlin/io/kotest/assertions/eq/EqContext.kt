package io.kotest.assertions.eq

class EqContext {
   private val visited = mutableListOf<Pair<Any?, Any?>>()

   fun push(actual: Any?, expected: Any?) = visited.add(actual to expected)

   fun pop() = visited.removeLastOrNull()

   fun isVisited(actual: Any?, expected: Any?) = visited.any { it.first === actual && it.second === expected }
}
