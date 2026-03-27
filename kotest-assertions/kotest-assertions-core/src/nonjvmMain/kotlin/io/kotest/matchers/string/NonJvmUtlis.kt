package io.kotest.matchers.string

internal actual fun describeBestFitForSubstringsInOrder(
   value: String,
   substrings: List<String>,
) : BestFitForSubstringsInOrderOutcome = BestFitForSubstringsInOrderOutcome.Ineligible("JVM only")
