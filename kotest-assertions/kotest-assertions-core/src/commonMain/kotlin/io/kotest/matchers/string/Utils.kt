package io.kotest.matchers.string

import io.kotest.assertions.print.print

internal expect fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
   matchOffset: MatchOffset,
) : BestFitForSubstringsInOrderOutcome

sealed interface BestFitForSubstringsInOrderOutcome {
   object Match : BestFitForSubstringsInOrderOutcome
   data class Mismatch(
      val bestFitIndexes: List<Int>,
      val indexesOfMatches: List<List<Int>>,
      ) : BestFitForSubstringsInOrderOutcome {
      val description: String = run {
         if(bestFitIndexes.isEmpty()) {
            "No matches found."
         } else {
            val bestFitDescriptionElements = (0 until indexesOfMatches.size).map { index ->
               if(index in bestFitIndexes) {
                  index.toString()
               } else {
                  "-"
               }
            }
            val bestFitDescription = "The best fit is the subset with the following indexes: ${bestFitDescriptionElements.print().value.replace("\"", "")}."

            val mismatchesDescription = (0 until indexesOfMatches.size).asSequence()
               .filter { it !in bestFitIndexes }
               .map { index ->
                  if(indexesOfMatches[index].isEmpty()) {
                     "Element[$index] not found"
                  } else {
                     "Element[$index] found at index(es): ${indexesOfMatches[index].print().value}"
                  }
               }.toList()

            (listOf(bestFitDescription)+mismatchesDescription).joinToString( "\n" )
         }
      }
   }
   data class Ineligible(val reason: String) : BestFitForSubstringsInOrderOutcome
   object TimedOut : BestFitForSubstringsInOrderOutcome
}
