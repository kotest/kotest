package io.kotest.matchers.collections.detailed.distance

import java.math.BigDecimal

interface IDistanceCalculator {
    fun compare(field: String, expected: Any?, actual: Any?): ComparisonResult
}

data class Distance(
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

        val CompleteMatch = Distance(COMPLETE_MATCH_VALUE)
        val CompleteMismatch = Distance(COMPLETE_MISMATCH_VALUE)
    }
}

