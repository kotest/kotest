package io.kotest.property.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BeforePermutationTest : FunSpec() {
   init {
      test("before should be called for each permutation") {
         var counter = 0
         permutations {
            iterations = 10
            beforePermutation {
               counter++
            }
            forEach {

            }
         }
         counter shouldBe 10
      }
   }
}
