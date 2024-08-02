package io.kotest.matchers.string

import io.kotest.submatching.PartialCollectionMatch

internal fun describeMatchedSubstrings(substr: String, matches: List<PartialCollectionMatch<Char>>): String {
   return matches.joinToString("\n") { match ->
      "Substring <\"${match.partOfValue.joinToString("")}\"> at indexes [${match.rangeOfExpected}] matches at indexes [${match.rangeOfValue}]"
   }
}

internal fun matchedSubstringsInContext(value: String, matches: List<PartialCollectionMatch<Char>>): String {
   return matches.joinToString("\n") { match ->
      val rangeOfMatch = match.rangeOfValue
      val margin = 5
      val rangeToPrint = maxOf(0, match.rangeOfValue.first)..minOf(match.rangeOfValue.last, value.length)
      "Substring at indexes [${match.rangeOfExpected}] matches value at indexes [${match.rangeOfValue}]\n" +
         "Where >>> denotes characters before, +++ denotes the match, and <<< denotes characters after"
   }
}

internal fun submatchWithMarginMapping(value: String, match: PartialCollectionMatch<Char>): String {
   val rangeOfMatch = match.rangeOfValue
   val margin = 5
   val rangeWithMargin = rangeWithMargin(rangeOfMatch, margin, maxIndex = value.length - 1)
   val rangeWithMarginMapping = rangeWithMarginMapping(rangeOfMatch, rangeWithMargin)
   val submatchWithMargin = value.substring(rangeWithMargin)
   return conflateSubMatchWithMapping(submatchWithMargin, rangeWithMarginMapping)
}

internal fun conflateSubMatchWithMapping(submatchWithMargin: String, rangeWithMarginMapping: String): String {
   require(submatchWithMargin.length == rangeWithMarginMapping.length) {
      "Both arguments should have same length, were respectively: ${submatchWithMargin.length} and ${rangeWithMarginMapping.length}"
   }
   val submatchLines = submatchWithMargin.split("\n")
   return submatchLines.flatMapIndexed { index, line ->
      val offsetForMapping: Int = submatchLines.filterIndexed { lineIndex, _ -> lineIndex < index }.sumOf { it.length }
      listOf(rangeWithMarginMapping.substring(offsetForMapping, offsetForMapping + line.length), line)
   }.joinToString("\n")
}

internal fun rangeWithMarginMapping(range: IntRange, rangeWithMargin: IntRange): String =
   rangeWithMargin.joinToString("") { index ->
      when {
         index < range.first -> ">"
         index in range -> "+"
         else -> "<"
      }
   }

internal fun rangeWithMargin(range: IntRange, margin: Int, maxIndex: Int): IntRange =
   maxOf(0, range.first - margin)..minOf(range.last + margin, maxIndex)

internal fun prefixIfNotEmpty(value: String, getPrefix: () -> String) =
   if(value.isEmpty()) "" else "${getPrefix()}$value"
