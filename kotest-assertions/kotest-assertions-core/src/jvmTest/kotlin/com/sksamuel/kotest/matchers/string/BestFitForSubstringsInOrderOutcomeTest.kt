package com.sksamuel.kotest.matchers.string

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.BestFitForSubstringsInOrderOutcome

class BestFitForSubstringsInOrderOutcomeTest : StringSpec() {
   init {
       "no matches found" {
          BestFitForSubstringsInOrderOutcome.Mismatch(
             bestFitIndexes = listOf(),
             indexesOfMatches = listOf(),
          ).description shouldBe "No matches found."
       }
   }
}
