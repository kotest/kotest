package io.kotest.matchers.string

internal actual fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
   matchOffset: MatchOffset,
) : BestFitForSubstringsInOrderOutcome = BestFitForSubstringsInOrderOutcome.Ineligible("JVM only")
