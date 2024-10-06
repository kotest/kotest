package io.kotest.property.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PermutationResultTest : FunSpec() {
   init {
      test("PermutationResult should contain eval count") {
         permutations {
            iterations = 22
         }.evaluations shouldBe 22
      }
   }
}
