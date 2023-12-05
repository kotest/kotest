package io.kotest.matchers.collections.detailed

import io.kotest.matchers.collections.detailed.distance.ComparisonResult
import io.kotest.matchers.collections.detailed.distance.findBestMatches

internal fun<T> findClosestMatchesForLists(expected: List<T>,
                               actual: List<T>,
                                  elementMatches:  List<MatchResultsOfSubLists>
): List<PossibleMatch> {
    val actualListElementsToMatch = actualListElementsToMatch(elementMatches, actual)
    return actualListElementsToMatch.flatMap { actualIndexed ->
        bestMatchesForActualElement(actualIndexed, expected)
    }
}

internal fun bestMatchesForActualElement(
   actualElement: IndexedElement,
   candidates: List<Any?>
): List<PossibleMatch> {
    val bestMatches = findBestMatches(actualElement.element, candidates)
    return bestMatches.map { bestMatch ->
        PossibleMatch(
            IndexedElement(bestMatch.index, candidates[bestMatch.index]),
            actualElement,
            bestMatch.comparisonResult
        )
    }
}

internal fun <T> actualListElementsToMatch(
    elementMatches: List<MatchResultsOfSubLists>,
    actual: List<T>
): List<IndexedElement> {
    val matchedActualIndexes = elementMatches.filter {
        it.match
    }.flatMap { rangeMatch ->
        rangeMatch.rightIndexes
    }.toSet()

    return actual.mapIndexedNotNull {
            index, element ->
            if(index in matchedActualIndexes) null
        else IndexedElement(index, element)
    }
}

internal data class IndexedElement(
    val index: Int,
    val element: Any?
)

internal data class PossibleMatch(
   val matchInExpected: IndexedElement,
   val actual: IndexedElement,
   val comparisonResult: ComparisonResult
)
