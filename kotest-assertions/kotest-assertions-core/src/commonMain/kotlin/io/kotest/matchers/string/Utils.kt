package io.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.print.print
import io.kotest.common.withNonVirtualTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

internal suspend fun describeBestFitForSubstringsInOrder(
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
      val bestFit = try {
         findBestFitForSubstringsInOrder(value, substrings)
      } catch (_: CancellationException) {
         return BestFitForSubstringsInOrderOutcome.TimedOut
      }
      if (bestFit == substrings.indices.toList() )
         BestFitForSubstringsInOrderOutcome.Success
      else
         BestFitForSubstringsInOrderOutcome.Failure("The best fit is the subset with the following indexes: ${bestFit.print().value}.")
   }
}

sealed interface BestFitForSubstringsInOrderOutcome {
   object Success : BestFitForSubstringsInOrderOutcome
   data class Failure(val description: String) : BestFitForSubstringsInOrderOutcome
   data class Ineligible(val reason: String) : BestFitForSubstringsInOrderOutcome
   object TimedOut : BestFitForSubstringsInOrderOutcome
}

internal suspend fun findBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : List<Int> {
   return withNonVirtualTimeout(AssertionsConfig.maxSubstringSearchDurationInMs.value.milliseconds) {
      val indexesOfMatches = allIndexesOfSubstrings(value, substrings)
      return@withNonVirtualTimeout powerSetIndexes(substrings.size)
         .firstOrNull { subset -> subsetFitsInOrder(indexesOfMatches, subset) }
         ?: emptyList()
   }
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
