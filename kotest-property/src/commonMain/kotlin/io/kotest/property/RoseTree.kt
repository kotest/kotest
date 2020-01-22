package io.kotest.property

data class RTree<out T>(val value: T, val children: List<RTree<T>>) {
   companion object {
      fun <T> empty(value: T) = RTree(value, emptyList())
   }
}

fun <T, U> RTree<T>.map(f: (T) -> U): RTree<U> {
   return RTree(f(value), children.map { it.map(f) })
}

fun <T> RTree<T>.filter(f: (T) -> Boolean): RTree<T> {
   return RTree(value, children.filter { f(it.value) }.map { it.filter(f) })
}
