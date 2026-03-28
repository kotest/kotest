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



//TODO: import this function from common module when that PR is merged

internal fun powerSetIndexes(size: Int): Sequence<List<Int>> = sequence {
   require(size > 0) { "Size should be positive, was: $size"}
   val elementsIncluded = MutableList(size) { true }
   val allIndexes = (0 until size).toList()
   yield(allIndexes)
   while(elementsIncluded.any { it }) {
      for (index in 0 until size) {
         if (elementsIncluded[index]) {
            elementsIncluded[index] = false
            yield(allIndexes.filterIndexed { i, _ -> elementsIncluded[i] })
            break
         } else {
            elementsIncluded[index] = true
         }
      }
   }
}

