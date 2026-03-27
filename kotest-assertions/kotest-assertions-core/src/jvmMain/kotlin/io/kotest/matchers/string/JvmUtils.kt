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
