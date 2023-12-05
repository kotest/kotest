package io.kotest.matchers.collections.detailed

private fun sortByCriteria(matches: List<List<MatchResultsOfSubLists>>): List<List<MatchResultsOfSubLists>> {
    return matches.sortedWith(compareBy({ -countMatchedItems(it) },
            { -maxLengthOfMatchedRange(it) },
            { countMatchedRanges(it) },
            { it.count() })
    )
}

internal fun bestTwoMatches(matches: List<List<MatchResultsOfSubLists>>): List<List<MatchResultsOfSubLists>> {
    val sortedMatches = sortByCriteria(matches)
    return listOf(sortedMatches.firstOrNull { it[0].match },
            sortedMatches.firstOrNull { !it[0].match })
            .filter { it != null }
            .map { it!! }
}

internal fun countMatchedItems(matches: List<MatchResultsOfSubLists>): Int = matches.filter { it.match }.sumOf { it.leftRange.length() }

internal fun maxLengthOfMatchedRange(matches: List<MatchResultsOfSubLists>): Int {
    @Suppress("DEPRECATION")
    val maxLength = matches.filter { it.match }.maxOfOrNull { it.leftRange.length() }
    return maxLength ?: 0
}

internal fun countMatchedRanges(matches: List<MatchResultsOfSubLists>): Int = matches.count {it.match}

internal val defaultMismatch = listOf(MatchResultsOfSubLists(false, 0..-1, 0..-1))

internal fun bestMatch(matches: List<List<MatchResultsOfSubLists>>) =
        sortByCriteria(matches).firstOrNull() ?: defaultMismatch
