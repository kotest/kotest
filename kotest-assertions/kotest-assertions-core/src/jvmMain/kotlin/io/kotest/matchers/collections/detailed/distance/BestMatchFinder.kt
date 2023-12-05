package io.kotest.matchers.collections.detailed.distance

import java.math.BigDecimal

internal fun findBestMatches(element: Any?, candidates: List<Any?>): List<IndexedComparisonResult> {
    val comparisonResults = candidates.mapIndexed { index, candidate ->
        IndexedComparisonResult(
            index,
            VanillaDistanceCalculator.compare("", candidate, element)
        )
    }

    val (completeMatches, partialMatches) = comparisonResults.filter {
        it.comparisonResult !is AtomicMismatch
    }.partition { it.comparisonResult is Match }

    if(completeMatches.isNotEmpty()) return completeMatches

    return partialMatches.asSequence()
        .mapNotNull {
            when(it.comparisonResult) {
                is MismatchByField -> IndexedMismatchByField(it.index, it.comparisonResult)
                else -> null
            }
        }
        .filter { it.comparisonResult.distance.distance > BigDecimal.ZERO }
        .topWithTiesBy {
            it.comparisonResult.distance.distance
        }.map {
            IndexedComparisonResult(it.index, it.comparisonResult)
        }
}

internal data class IndexedComparisonResult(
    val index: Int,
    val comparisonResult: ComparisonResult
)

internal data class IndexedMismatchByField(
    val index: Int,
    val comparisonResult: MismatchByField
)
