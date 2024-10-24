package io.kotest.similarity

import io.kotest.assertions.AssertionsConfig
import java.math.BigDecimal

internal interface IDistanceCalculator {
    fun compare(field: String, expected: Any?, actual: Any?): ComparisonResult
}

internal data class Distance(
    val distance: BigDecimal
) {
    init {
        require(distance in COMPLETE_MISMATCH_VALUE..COMPLETE_MATCH_VALUE) {
            "Distance must be between 0 and 1, was: $distance"
        }
    }
    companion object {
        val COMPLETE_MISMATCH_VALUE: BigDecimal = BigDecimal.ZERO
        val COMPLETE_MATCH_VALUE: BigDecimal = BigDecimal.ONE
        val PERCENT_TO_DISTANCE: BigDecimal = BigDecimal("0.01")

        val CompleteMatch = Distance(COMPLETE_MATCH_VALUE)
        val CompleteMismatch = Distance(COMPLETE_MISMATCH_VALUE)
    }

   fun aboveThresholdForDataClasses(): Boolean =
      distance >= BigDecimal.valueOf(AssertionsConfig.similarityThresholdInPercent.value.toLong()) * PERCENT_TO_DISTANCE

   fun aboveThresholdForStrings(): Boolean =
      distance >= BigDecimal.valueOf(AssertionsConfig.similarityThresholdInPercentForStrings.value.toLong()) * PERCENT_TO_DISTANCE
}

