package io.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig
import io.kotest.common.withNonVirtualTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.runBlocking

internal actual fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : BestFitForSubstringsInOrderOutcome = when {
   value.length > AssertionsConfig.maxValueSubmatchingSize.value ->
      BestFitForSubstringsInOrderOutcome.Ineligible("value length (${value.length}) exceeds maximum allowed (${AssertionsConfig.maxValueSubmatchingSize.value})")
   substrings.isEmpty() ->
      BestFitForSubstringsInOrderOutcome.Ineligible("substring is empty")
   substrings.any { it.isEmpty() } ->
      BestFitForSubstringsInOrderOutcome.Ineligible("at least one substring is empty")
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
         BestFitForSubstringsInOrderOutcome.Match
      else
         BestFitForSubstringsInOrderOutcome.Mismatch(
            bestFit,
            )
   }
}

internal fun findBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : List<Int> {
   return runBlocking {
      withNonVirtualTimeout(AssertionsConfig.maxSubstringSearchDurationInMs.value.milliseconds) {
         val indexesOfMatches = allIndexesOfSubstrings(value, substrings)
         return@withNonVirtualTimeout powerSetIndexes(substrings.size)
            .firstOrNull { subset -> subsetFitsInOrder(indexesOfMatches, subset) }
            ?: emptyList()
      }
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

internal fun subsetFitsInOrder(indexesOfMatches: List<List<Int>>, subset: List<Int>) : Boolean {
   var nextIndex = -1
   (0 until subset.size).forEach { i ->
      val nextIndexes = indexesOfMatches[subset[i]]
      val nextIndexInSubset = nextIndexes.firstOrNull { it >= nextIndex } ?: return false
      nextIndex = nextIndexInSubset + 1
   }
   return true
}
