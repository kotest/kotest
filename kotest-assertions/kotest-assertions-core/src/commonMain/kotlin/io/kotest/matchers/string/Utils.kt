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

internal fun allIndexesOfSubstrings(value: String, substrings: List<String>) =
   substrings.map { substring -> allIndexesOf(value, substring) }

internal fun allIndexesOf(value: String, substring: String): List<Int> {
   val indexes = mutableListOf<Int>()
   var index = value.indexOf(substring)
   while (index >= 0 && indexes.size < 100) {
      indexes.add(index)
      index = value.indexOf(substring, index + 1)
   }
   return indexes
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

internal fun subsetFitsInOrder(indexesOfMatches: List<List<Int>>, subset: List<Int>) : Boolean {
   var nextIndex = -1
   (0 until subset.size).forEach { i ->
      val nextIndexes = indexesOfMatches[subset[i]]
      val nextIndexInSubset = nextIndexes.firstOrNull { it >= nextIndex } ?: return false
      nextIndex = nextIndexInSubset + 1
   }
   return true
}
