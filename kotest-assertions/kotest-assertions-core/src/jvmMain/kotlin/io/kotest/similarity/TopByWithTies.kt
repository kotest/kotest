package io.kotest.similarity

/**
 * Return a [List] containing all the values with the highest ranking provided by [rankingTransform]
 *
 * If more than one element havs the same ranking returned by [keySelector] their order is preserved.
 *
 * The operation is _stateful_ and  _terminal_.
 */
internal inline fun<T, R: Comparable<R>> Sequence<T>.topWithTiesBy(rankingTransform: (T) -> R ): List<T> {
    val ret = mutableListOf<T>()
    var maxRanking: R? = null
    this.forEach { element ->
        val ranking = rankingTransform(element)
        if(maxRanking == null || ranking > maxRanking!!) {
            maxRanking = ranking
            ret.clear()
        }
        if(ranking == maxRanking) ret.add(element)
     }
    return ret
}
