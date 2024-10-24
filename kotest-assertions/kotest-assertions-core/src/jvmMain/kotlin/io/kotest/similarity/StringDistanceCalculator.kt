package io.kotest.similarity

import io.kotest.submatching.PartialCollectionMatch
import io.kotest.submatching.describePartialMatchesInStringForSlice
import java.math.BigDecimal
import java.math.RoundingMode

internal fun matchNotNullStrings(field: String, expected: String, actual: String): ComparisonResult = when {
   expected == actual -> Match(field, expected)
   else -> {
      val comparison = describePartialMatchesInStringForSlice(expected, actual, forceComparison = true)
      val ratioOfPartialMatches = ratioOfPartialMatchesInString(
         comparison.partialMatches,
         expected,
         actual
      )
      val partialMatchesAreUnordered = partialMatchesAreUnordered(comparison.partialMatches)
      val discountForUnorderedPartialMatches = if(partialMatchesAreUnordered) BigDecimal("0.8") else BigDecimal.ONE
      val distance = Distance(ratioOfPartialMatches.multiply(discountForUnorderedPartialMatches))
      StringMismatch(field, expected, actual, comparison.toString(), distance)
   }
}

internal fun partialMatchesAreUnordered(partialMatches: List<PartialCollectionMatch>): Boolean {
   return partialMatches.sortedBy { it.matchedElement.startIndexInExpected }
      .zipWithNext().any { (first, second) ->
      first.matchedElement.startIndexInValue > second.matchedElement.startIndexInValue
   }
}

internal fun ratioOfPartialMatchesInString(
   partialMatches: List<PartialCollectionMatch>,
   expected: String,
   actual: String,
   ): BigDecimal {
   val maxLength = listOf(expected, actual).maxOf { it.length }
   val numberOfMatchedCharacters = partialMatches.sumOf { it.length }
   return BigDecimal.valueOf(numberOfMatchedCharacters.toLong())
      .divide(BigDecimal.valueOf(maxLength.toLong()), 2, RoundingMode.HALF_DOWN)
}
