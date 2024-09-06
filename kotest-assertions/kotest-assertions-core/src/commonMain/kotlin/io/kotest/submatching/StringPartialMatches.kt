package io.kotest.submatching

import io.kotest.assertions.print.print

internal fun describePartialMatchesInString(expectedSlice: String, value: String): PartialMatchesInCollectionDescription {
   val minLength = maxOf(expectedSlice.length / 3, 2)
   val partialMatches = findPartialMatches(expectedSlice.toList(), value.toList(), minLength = minLength).take(9)
   if(partialMatches.isEmpty()) {
      return PartialMatchesInCollectionDescription("", "")
   }
   val partialMatchesList = partialMatches.withIndex().joinToString("\n") { indexedValue ->
      "Match[${indexedValue.index}]: expected[${indexedValue.value.rangeOfExpected}] matched actual[${indexedValue.value.rangeOfValue}]"
   }
   val allUnderscores = getAllUnderscores(value.length, partialMatches)
   val lines = value.lines()
   if(lines.size == 1) {
      return PartialMatchesInCollectionDescription(partialMatchesList, (lines + allUnderscores).joinToString("\n"))
   }
   val allUnderscoresSplitPerLine = allUnderscores.map { splitUnderscoreToFitLines(lines, it) }
   val valueAndUnderscores = lines.flatMapIndexed { index, line ->
      listOf("Line[$index] =\"$line\"") + allUnderscoresSplitPerLine.mapIndexed { matchIndex, underscores ->
         "Match[$matchIndex]= ${underscores[index]}"
      }
   }.joinToString("\n")
   return PartialMatchesInCollectionDescription(partialMatchesList, valueAndUnderscores)
}

internal fun getAllUnderscores(valueLength: Int, partialMatches: List<PartialCollectionMatch<Char>>): List<String> {
   return partialMatches.map { underscoreSubstring(valueLength, it.rangeOfValue.first, it.rangeOfValue.last) }
}

internal data class PartialMatchesInCollectionDescription(
   val partialMatchesList: String,
   val partialMatchesDescription: String
)

internal fun splitUnderscoreToFitLines(
   lines: List<String>,
   underscoredLine: String
): List<String> {
   val linesStarts = lines.runningFold(0) {
         acc, line -> acc + line.length + lineSeparatorLength()
   }
   return lines.mapIndexed() { index: Int, line: String ->
      underscoredLine.substring(linesStarts[index] until  linesStarts[index] + line.length)
   }
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
