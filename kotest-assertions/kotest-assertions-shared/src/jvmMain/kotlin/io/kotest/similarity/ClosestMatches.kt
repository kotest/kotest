package io.kotest.similarity

internal fun<T> closestMatches(expected: Set<T>, actual: T): List<PairComparison<T>> {
   return expected.asSequence().mapNotNull { candidate ->
            val comparisonResult = VanillaDistanceCalculator.compare("", candidate, actual)
            if (comparisonResult is MismatchByField &&
               comparisonResult.distance.distance > Distance.COMPLETE_MISMATCH_VALUE) {
               PairComparison(actual, candidate, comparisonResult)
            } else null
         }.topWithTiesBy {
            it.comparisonResult.distance.distance
         }
}

internal data class PairComparison<T>(
   val value: T,
   val possibleMatch: T,
   val comparisonResult: MismatchByField
)
