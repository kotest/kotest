package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.BestFitForSubstringsInOrderOutcome
import io.kotest.matchers.string.shouldContain

class BestFitForSubstringsInOrderOutcomeTest : StringSpec() {
   init {
       "no matches found" {
          BestFitForSubstringsInOrderOutcome.Mismatch(
             bestFitIndexes = listOf(),
             indexesOfMatches = listOf(),
          ).description shouldBe "No matches found."
       }
      "found best fit, some elements not matched" {
         val actual = BestFitForSubstringsInOrderOutcome.Mismatch(
            bestFitIndexes = listOf(0, 2, 4),
            indexesOfMatches = listOf(
               listOf(0),
               listOf(),
               listOf(3),
               listOf(),
               listOf(5),
            ),
         ).description
         actual shouldContain "The best fit is the subset with the following indexes: [0, -, 2, -, 4]."
         actual shouldContain "Element[1] not found"
         actual shouldContain "Element[3] not found"
      }
      "found best fit, some elements matched out of order" {
         val actual = BestFitForSubstringsInOrderOutcome.Mismatch(
            bestFitIndexes = listOf(0, 2, 4),
            indexesOfMatches = listOf(
               listOf(0),
               listOf(),
               listOf(3),
               listOf(1),
               listOf(5),
            ),
         ).description
         actual shouldContain "The best fit is the subset with the following indexes: [0, -, 2, -, 4]."
         actual shouldContain "Element[1] not found"
         actual shouldContain "Element[3] found at index(es): [1]"
      }
   }
}
