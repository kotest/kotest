package io.kotest.permutations.checks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MaxDiscardCheckTest : FunSpec({

   test("MaxDiscardCheck happy path") {
      MaxDiscardCheck.discardPercentage(0, 0) shouldBe 0
      MaxDiscardCheck.discardPercentage(12, 44) shouldBe 0
   }

})
