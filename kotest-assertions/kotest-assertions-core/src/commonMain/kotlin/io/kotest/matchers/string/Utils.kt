package io.kotest.matchers.string

import io.kotest.assertions.print.print

internal expect fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : BestFitForSubstringsInOrderOutcome

sealed interface BestFitForSubstringsInOrderOutcome {
   object Match : BestFitForSubstringsInOrderOutcome
   data class Mismatch(
      val matchedIndexes: List<Int>,
      ) : BestFitForSubstringsInOrderOutcome {
      val description: String = "The best fit is the subset with the following indexes: ${matchedIndexes.print().value}."
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

