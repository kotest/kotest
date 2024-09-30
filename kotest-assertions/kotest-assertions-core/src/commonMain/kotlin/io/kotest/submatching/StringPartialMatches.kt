package io.kotest.submatching

import io.kotest.assertions.AssertionsConfig

internal fun describePartialMatchesInString(expectedSlice: String, value: String): PartialMatchesInCollectionDescription {
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
      "Match[${indexedValue.index}]: expected[${indexedValue.value.rangeOfExpected}] matched actual[${indexedValue.value.rangeOfValue}]"
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

private fun substringNotEligibleForSubmatching(value: String) =
   AssertionsConfig.minSubtringSubmatchingSize.value > value.length ||
      value.length > AssertionsConfig.maxSubtringSubmatchingSize.value


private fun valueNotEligibleForSubmatching(value: String) =
   AssertionsConfig.minValueSubmatchingSize.value > value.length ||
      value.length > AssertionsConfig.maxValueSubmatchingSize.value

internal fun getAllUnderscores(valueLength: Int, partialMatches: List<PartialCollectionMatch<Char>>): List<String> {
   return partialMatches.map { underscoreSubstring(valueLength, it.rangeOfValue.first, it.rangeOfValue.last) }
}

internal data class PartialMatchesInCollectionDescription(
   val partialMatchesList: String,
   val partialMatchesDescription: String
) {
   override fun toString(): String = listOf(partialMatchesList, partialMatchesDescription)
      .filter { it.isNotEmpty() }.joinToString("\n")
}

internal fun underscoreSubstring(
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
