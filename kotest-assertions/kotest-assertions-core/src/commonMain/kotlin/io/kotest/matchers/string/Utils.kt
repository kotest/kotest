package io.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig

internal fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : BestFitForSubstringsInOrderOutcome = when {
   value.length > AssertionsConfig.maxValueSubmatchingSize.value ->
      BestFitForSubstringsInOrderOutcome.Ineligible("value length (${value.length}) exceeds maximum allowed (${AssertionsConfig.maxValueSubmatchingSize.value})")
   substrings.size > AssertionsConfig.maxSubstringCount.value ->
      BestFitForSubstringsInOrderOutcome.Ineligible("substring count (${substrings.size}) exceeds maximum allowed (${AssertionsConfig.maxSubstringCount.value})")
   substrings.any { it.length > AssertionsConfig.maxSubstringSize.value } ->
      BestFitForSubstringsInOrderOutcome.Ineligible("at least one substring length exceeds maximum allowed (${AssertionsConfig.maxSubstringSize.value})")
   else -> {
      val bestFit = findBestFitForSubstringsInOrder(value, substrings)
      if (bestFit == substrings.indices.toList() )
         BestFitForSubstringsInOrderOutcome.Success
      else
         BestFitForSubstringsInOrderOutcome.Failure("best fit for substrings in order is: ${bestFit.joinToString(", ")}")
   }
}

sealed interface BestFitForSubstringsInOrderOutcome {
   object Success : BestFitForSubstringsInOrderOutcome
   data class Failure(val description: String) : BestFitForSubstringsInOrderOutcome
   data class Ineligible(val reason: String) : BestFitForSubstringsInOrderOutcome
}

internal fun findBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : List<Int> {
   val indexesOfMatches = allIndexesOfSubstrings(value, substrings)
   return powerSetIndexes(substrings.size)
      .firstOrNull { subset -> subsetFitsInOrder(indexesOfMatches, subset) }
      ?: emptyList()
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
