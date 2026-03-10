package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.assertions.AssertionsConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.BestFitForSubstringsInOrderOutcome
import io.kotest.matchers.string.describeBestFitForSubstringsInOrder
import io.kotest.matchers.types.shouldBeInstanceOf

class DescribeBestFitForSubstringsInOrderTest : StringSpec(){
   init {
      "return Ineligible when value length exceeds maxValueSubmatchingSize" {
         val maxSize = AssertionsConfig.maxValueSubmatchingSize.value
         val longValue = "a".repeat(maxSize + 1)
         val result = describeBestFitForSubstringsInOrder(longValue, listOf("a"))
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
            .reason shouldBe "value length (${longValue.length}) exceeds maximum allowed ($maxSize)"
      }

      "return Ineligible when substring count exceeds maxSubstringCount" {
         val maxCount = AssertionsConfig.maxSubstringCount.value
         val substrings = List(maxCount + 1) { "sub$it" }
         val result = describeBestFitForSubstringsInOrder("value", substrings)
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
            .reason shouldBe "substring count (${substrings.size}) exceeds maximum allowed ($maxCount)"
      }

      "return Ineligible when any substring length exceeds maxSubstringSize" {
         val maxSize = AssertionsConfig.maxSubstringSize.value
         val longSubstring = "a".repeat(maxSize + 1)
         val result = describeBestFitForSubstringsInOrder("value", listOf("wolf", longSubstring))
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Ineligible>()
            .reason shouldBe "at least one substring length exceeds maximum allowed ($maxSize)"
      }

      "return Success when all substrings are found in order" {
         val result = describeBestFitForSubstringsInOrder("Call it a day", listOf("Call", "it", "a", "day"))
         result shouldBe BestFitForSubstringsInOrderOutcome.Success
      }

      "return Failure when substrings are not found in order" {
         val result = describeBestFitForSubstringsInOrder("Call it a day", listOf("Call", "day", "it"))
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Failure>()
            .description shouldBe "The best fit is the subset with the following indexes: [0, 2]."
      }

      "return Failure when a substring is not found in value" {
         val result = describeBestFitForSubstringsInOrder("Call it a day", listOf("Call", "it", "a", "Day"))
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Failure>()
            .description shouldBe "The best fit is the subset with the following indexes: [0, 1, 2]."
      }

      "return Success for single substring that exists in value" {
         val result = describeBestFitForSubstringsInOrder("I've been in this boat", listOf("boat"))
         result shouldBe BestFitForSubstringsInOrderOutcome.Success
      }

      "return Failure for single substring that does not exist in value" {
         val result = describeBestFitForSubstringsInOrder("No pain no gain", listOf("yes"))
         result.shouldBeInstanceOf<BestFitForSubstringsInOrderOutcome.Failure>()

      }

      "return Success when value exactly matches the single substring" {
         val result = describeBestFitForSubstringsInOrder("exact", listOf("exact"))
         result shouldBe BestFitForSubstringsInOrderOutcome.Success
      }

      "return Success when substrings are adjacent in value" {
         val result = describeBestFitForSubstringsInOrder("worldwide", listOf("world", "wide"))
         result shouldBe BestFitForSubstringsInOrderOutcome.Success
      }
   }
}
