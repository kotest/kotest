package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PermutationResultTest : FunSpec() {
   init {
      test("PermutationResult should contain eval count") {
         permutations {
            iterations = 22
         }.iterations shouldBe 22
      }
   }
}
