package io.kotest.submatching

import io.kotest.assertions.AssertionsConfig

class StringPartialMatch(val expected: String, val value: String) {
   private val description: PartialMatchesInCollectionDescription by lazy {
      describePartialMatchesInStringForSlice(expected, value)
   }
   val matched: Boolean by lazy { description.partialMatchesList.isNotEmpty() }
   val descriptionString: String by lazy { description.toString() }
   fun error(): AssertionError = AssertionError("")
}

fun describePartialMatchesInStringForSlice(expectedSlice: String, value: String) =
   describePartialMatchesInString(expectedSlice, value, PartialMatchType.Slice)

fun describePartialMatchesInStringForSuffix(expectedSlice: String, value: String) =
   describePartialMatchesInString(expectedSlice, value, PartialMatchType.Suffix)

fun describePartialMatchesInStringForPrefix(expectedSlice: String, value: String) =
   describePartialMatchesInString(expectedSlice, value, PartialMatchType.Prefix)

fun describePartialMatchesInString(expectedSlice: String, value: String, type: PartialMatchType): PartialMatchesInCollectionDescription {
   if(!AssertionsConfig.enabledSubmatchesInStrings.value ||
         substringNotEligibleForSubmatching(expectedSlice) ||
         valueNotEligibleForSubmatching(value)
      ) {
      return PartialMatchesInCollectionDescription("", "")
   }
   val minLength = maxOf(expectedSlice.length / 3, 2)
   val partialMatches = findPartialMatches(expectedSlice.toList(), value.toList(), minLength = minLength).take(9)
   if(partialMatches.isEmpty()) {
      return PartialMatchesInCollectionDescription("", "")
   }
   val partialMatchesList = partialMatches.withIndex().joinToString("\n") { indexedValue ->
      "Match[${indexedValue.index}]: ${describeMatchedSlice(expectedSlice, indexedValue.value.rangeOfExpected, type)} matched actual[${indexedValue.value.rangeOfValue}]"
   }
   val allUnderscores = getAllUnderscores(value.length, partialMatches)
   val lineIndexRanges = indexRangesOfLines(value)
   val valueAndUnderscores = lineIndexRanges.mapIndexed { index, indexRange ->
      listOf("Line[$index] =\"${takeIndexRange(value, indexRange)}\"") + allUnderscores.mapIndexed { matchIndex, underscores ->
         "Match[$matchIndex]= ${takeIndexRange(underscores, indexRange)}"
      }
   }.flatten().joinToString("\n")
   return PartialMatchesInCollectionDescription(partialMatchesList, valueAndUnderscores)
}

internal fun describeMatchedSlice(expectedSlice: String, range: IntRange, type: PartialMatchType): String {
   return when {
      range == expectedSlice.indices -> "whole ${type.description}"
      else -> "part of ${type.description} with indexes [$range]"
   }
}

sealed interface PartialMatchType {
   val description: String

   data object Prefix: PartialMatchType {
      override val description: String = "prefix"
   }

   data object Suffix: PartialMatchType {
      override val description: String = "suffix"
   }

   data object Slice: PartialMatchType {
      override val description: String = "slice"
   }
}

private fun substringNotEligibleForSubmatching(value: String) =
   AssertionsConfig.minSubtringSubmatchingSize.value > value.length ||
      value.length > AssertionsConfig.maxSubtringSubmatchingSize.value


private fun valueNotEligibleForSubmatching(value: String) =
   AssertionsConfig.minValueSubmatchingSize.value > value.length ||
      value.length > AssertionsConfig.maxValueSubmatchingSize.value

internal fun getAllUnderscores(valueLength: Int, partialMatches: List<PartialCollectionMatch<Char>>): List<String> {
   return partialMatches.map { underscoreSubstring(valueLength, it.rangeOfValue.first, it.rangeOfValue.last) }
}

data class PartialMatchesInCollectionDescription(
   val partialMatchesList: String,
   val partialMatchesDescription: String
) {
   override fun toString(): String = listOf(partialMatchesList, partialMatchesDescription)
      .filter { it.isNotEmpty() }.joinToString("\n")
}

fun underscoreSubstring(
   valueLength: Int,
   fromIndex: Int,
   toIndex: Int
): String {
   val indexRange = fromIndex .. toIndex
   return (0 until valueLength).map { index ->
      if(index in indexRange) "+" else "-"
   }.joinToString("")
}

internal data class IndexRange(
   val fromIndex: Int,
   val toIndex: Int,
)

internal fun indexRangesOfLines(value: String): Sequence<IndexRange> {
   var fromIndex: Int? = null
   return sequence {
      "$value\n".forEachIndexed { index, c ->
         if(c in listOf('\r', '\n')) {
            if(fromIndex != null) {
               yield(IndexRange(fromIndex!!, index - 1))
               fromIndex = null
            }
         } else {
            fromIndex = fromIndex ?: index
         }
      }
    }
}

internal fun splitByIndexRanges(value: String, indexRanges: List<IndexRange>): List<String> {
   val lastRange = indexRanges.lastOrNull() ?: return listOf()
   require(lastRange.toIndex < value.length) {
      "Last range: $lastRange exceeds value length: ${value.length}"
   }
   return indexRanges.map { takeIndexRange(value, it) }
}

private fun takeIndexRange(value: String, it: IndexRange) =
   value.substring(it.fromIndex, it.toIndex + 1)
