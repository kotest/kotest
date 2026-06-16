package io.kotest.assertions.similarity

import io.kotest.assertions.submatching.PartialCollectionMatch
import io.kotest.assertions.submatching.describePartialMatchesInStringForSlice
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
      if(distance.aboveThresholdForStrings()) {
         StringMismatch(field, expected, actual, comparison.toString(), distance)
      } else {
         AtomicMismatch(field, expected, actual)
      }
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
   val numberOfMatchedCharacters = partialMatches.filterOverlapping().sumOf { it.length }
   return BigDecimal.valueOf(numberOfMatchedCharacters.toLong())
      .divide(BigDecimal.valueOf(maxLength.toLong()), 2, RoundingMode.HALF_DOWN)
}

// In case of overlapping partial matches, we eliminate the overlapping ones and only include the longest match
// for any given slice of the expected collection.
private fun List<PartialCollectionMatch>.filterOverlapping(): List<PartialCollectionMatch> {
   val sortedByLength = this.sortedBy { it.length }
   val nonOverlappingMatches = mutableListOf<PartialCollectionMatch>()
   for (match in sortedByLength) {
      if (nonOverlappingMatches.all { it.rangeOfExpected.intersect(match.rangeOfExpected).isEmpty() }) {
         nonOverlappingMatches.add(match)
      }
   }
   return nonOverlappingMatches
}
