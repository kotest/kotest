package io.kotest.similarity

import io.kotest.submatching.describePartialMatchesInStringForSlice

//internal fun matchNotNullStrings(field: String, expected: String, actual: String): ComparisonResult = when {
//   expected == actual -> Match(field, expected)
//   else -> {
//      val orderedFields: List<String> = listOf(expected, actual).sortedBy { it.length }
//      val shorter = orderedFields[0]
//      val longer = orderedFields[1]
//      val comparison = describePartialMatchesInStringForSlice()
//      val distance = Distance(expected.levenshtein(actual))
//      MismatchByField(field, expected, actual, emptyList(), distance)
//   }
//}
