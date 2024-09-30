package io.kotest.similarity

import io.kotest.assertions.AssertionsConfig

actual fun<T> possibleMatchesDescription(actual: Set<T>, expected: T): String {
   val possibleMatches = closestMatches(actual, expected)
   return if(possibleMatches.isEmpty()) ""
   else {
      val someEntriesSkippedDescription = if(AssertionsConfig.maxSimilarityPrintSize.value < possibleMatches.size) {
         "\nPrinted first ${AssertionsConfig.maxSimilarityPrintSize.value} similarities out of ${possibleMatches.size}, (set the 'kotest.assertions.similarity.print.size' JVM property to see full output for similarity)"
      }
      else ""
      "\n${possibleMatches.take(AssertionsConfig.maxSimilarityPrintSize.value).joinToString("\n\n"){it.comparisonResult.description()}}$someEntriesSkippedDescription"
   }
}

