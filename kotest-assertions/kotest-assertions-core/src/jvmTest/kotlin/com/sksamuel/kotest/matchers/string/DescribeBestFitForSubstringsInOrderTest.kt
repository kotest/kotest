package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.BestFitForSubstringsInOrderOutcome
import io.kotest.matchers.string.describeBestFitForSubstringsInOrder
import io.kotest.matchers.types.shouldBeInstanceOf

class DescribeBestFitForSubstringsInOrderTest : StringSpec()
{
   init {
      "return Match when all substrings are found in order" {
         val value = "no pain no gain"
         val substrings = listOf("pain", "no", "gain")
         val outcome = describeBestFitForSubstringsInOrder(value, substrings, { 1 })
         outcome shouldBe BestFitForSubstringsInOrderOutcome.Match
      }

      "return Mismatch with best fit indexes when not all substrings are found in order" {
         val value = "no pain no gain"
         val substrings = listOf("no", "no", "pain")
         val outcome = describeBestFitForSubstringsInOrder(value, substrings, { 1 })
         assert(outcome is BestFitForSubstringsInOrderOutcome.Mismatch)
         val mismatchOutcome = outcome as BestFitForSubstringsInOrderOutcome.Mismatch
         mismatchOutcome.bestFitIndexes shouldBe listOf(1, 2)
      }

      "return Ineligible when value length exceeds maximum allowed" {
         val value = "a".repeat(AssertionsConfig.maxValueSubmatchingSize.value + 1)
         val substrings = listOf("a")
         val outcome = describeBestFitForSubstringsInOrder(value, substrings, { 1 })
         outcome.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
      }

      "return Ineligible when no substrings provided" {
         val outcome = describeBestFitForSubstringsInOrder("Call it a day", emptyList(), { 1 })
         outcome.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
      }

      "return Ineligible when some substrings are empty" {
         val outcome = describeBestFitForSubstringsInOrder("Call it a day", listOf("Call", "", "day"), { 1 })
         outcome.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
      }

      "return Ineligible when substring count exceeds maximum allowed" {
         val value = "abc"
         val substrings = List(AssertionsConfig.maxSubstringCount.value + 1) { "a" }
         val outcome = describeBestFitForSubstringsInOrder(value, substrings, { 1 })
         outcome.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
      }

      "return Ineligible when at least one substring length exceeds maximum allowed" {
         val value = "abc"
         val longSubstring = "a".repeat(AssertionsConfig.maxSubstringSize.value + 1)
         val substrings = listOf(longSubstring)
         val outcome = describeBestFitForSubstringsInOrder(value, substrings, { 1 })
         outcome.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
      }
   }
}
