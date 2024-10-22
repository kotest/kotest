package io.kotest.similarity

import io.kotest.submatching.describePartialMatchesInStringForSlice
import java.math.BigDecimal

internal fun matchNotNullStrings(field: String, expected: String, actual: String): ComparisonResult = when {
   expected == actual -> Match(field, expected)
   else -> {
      val orderedFields: List<String> = listOf(expected, actual).sortedBy { it.length }
      val shorter = orderedFields[0]
      val longer = orderedFields[1]
      val comparison = describePartialMatchesInStringForSlice(shorter, longer)
//      val ordered =
      val distance = Distance(BigDecimal.ZERO)
      MismatchByField(field, expected, actual, emptyList(), distance)
   }
}
