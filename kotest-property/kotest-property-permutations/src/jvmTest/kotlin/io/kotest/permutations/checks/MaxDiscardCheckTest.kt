package io.kotest.permutations.checks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MaxDiscardCheckTest : FunSpec({

   test("discardPercentage happy path") {
      MaxDiscardCheck.discardPercentage(0, 0) shouldBe 0
      MaxDiscardCheck.discardPercentage(12, 44) shouldBe 27
      MaxDiscardCheck.discardPercentage(12, 12) shouldBe 100
   }

   test("discardPercentage should still work if no iterations have yet happened") {
      MaxDiscardCheck.discardPercentage(1, 0) shouldBe 100
   }

   test("error message") {
      MaxDiscardCheck.errorMessage(12, 44, 10, 27) shouldBe "Percentage of discarded inputs (12/44 27%) exceeds max (10%).\nAdjust your generators to increase the probability of an acceptable value, or increase the max discard percentage in permutation config.\n"
   }
})
