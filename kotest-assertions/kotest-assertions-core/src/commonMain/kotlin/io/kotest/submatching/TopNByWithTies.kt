package io.kotest.submatching

/**
 * Return a [List] containing all the values with N highest rankings provided by [rankingTransform]
 *
 * If more than one element has the same ranking returned by [keySelector] their order is preserved.
 *
 * The operation is _stateful_ and  _terminal_.
 */
internal inline fun<T, R: Comparable<R>> List<T>.topNWithTiesBy(depth: Int, rankingTransform: (T) -> R ): List<T> {
   val elementsByRank: Map<R, List<T>> = this.groupBy(rankingTransform)
   val topRanks = elementsByRank.keys.sorted().reversed().take(depth)
   return topRanks.flatMap { key -> elementsByRank.getOrElse(key) { listOf() } }
}
