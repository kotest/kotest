package io.kotest.similarity

import io.kotest.assertions.AssertionsConfig
import java.math.BigDecimal

internal fun<T> closestMatches(actual: Set<T>, expected: T): List<PairComparison<T>> {
   return actual.asSequence().mapNotNull { candidate ->
            val comparisonResult = VanillaDistanceCalculator.compare("", expected, candidate)
            if (comparisonResult is MismatchByField &&
               comparisonResult.distance.distance > maxOf(
                  Distance.COMPLETE_MISMATCH_VALUE,
                  BigDecimal(AssertionsConfig.similarityThresholdInPercent.value) * Distance.PERCENT_TO_DISTANCE,
               )
            ) {
               PairComparison(expected, candidate, comparisonResult)
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
