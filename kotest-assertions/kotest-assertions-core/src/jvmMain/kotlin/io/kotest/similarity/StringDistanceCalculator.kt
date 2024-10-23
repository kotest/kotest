package io.kotest.similarity

import io.kotest.submatching.PartialCollectionMatch
import io.kotest.submatching.describePartialMatchesInStringForSlice
import java.math.BigDecimal
import java.math.RoundingMode

internal fun matchNotNullStrings(field: String, expected: String, actual: String): ComparisonResult = when {
   expected == actual -> Match(field, expected)
   else -> {
      val orderedFields: List<String> = listOf(expected, actual).sortedBy { it.length }
      val shorter = orderedFields[0]
      val longer = orderedFields[1]
      val maxLength = listOf(expected, actual).maxOf { it.length }
      val comparison = describePartialMatchesInStringForSlice(shorter, longer)
      val numberOfMatchedCharacters = comparison.partialMatches.sumOf { it.length }
      val ratioOfPartialMatches = BigDecimal.valueOf(numberOfMatchedCharacters.toLong())
         .divide(BigDecimal.valueOf(maxLength.toLong()))
//      val partialMatchesAreUnordered = comparison.partialMatches.zipWithNext().any { (first, second) ->
//         first.first > second.first
//      }
      val distance = Distance(BigDecimal.ZERO)
      MismatchByField(field, expected, actual, emptyList(), distance)
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
