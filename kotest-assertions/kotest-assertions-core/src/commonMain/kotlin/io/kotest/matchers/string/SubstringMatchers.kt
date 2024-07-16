package io.kotest.matchers.string

import io.kotest.submatching.findPartialMatches
import io.kotest.submatching.PartialCollectionMatch
import io.kotest.submatching.topNWithTiesBy

internal fun describeMatchedSubstrings(substr: String, matches: List<PartialCollectionMatch<Char>>): String {
   return matches.joinToString("\n") { match ->
      "Substring <\"${match.partOfValue.joinToString("")}\"> at indexes [${match.rangeOfValue}] matches at indexes [${match.rangeOfTarget}]"
   }
}

internal fun matchedSubstringsInContext(value: String, matches: List<PartialCollectionMatch<Char>>): String {
   return matches.joinToString("\n") { match ->
      val rangeOfMatch = match.rangeOfTarget
      val margin = 5
      val rangeToPrint = maxOf(0, match.rangeOfTarget.first)..minOf(match.rangeOfTarget.last, value.length)
      "Substring at indexes [${match.rangeOfValue}] matches value at indexes [${match.rangeOfTarget}]\n" +
         "Where >>> denotes characters before, +++ denotes the match, and <<< denotes characters after"
   }
}

internal fun submatchWithMarginMapping(value: String, match: PartialCollectionMatch<Char>): String {
   val rangeOfMatch = match.rangeOfTarget
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
