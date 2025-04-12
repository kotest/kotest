package io.kotest.permutations

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class AfterPermutationTest : FunSpec() {
   init {
      test("before should be called for each permutation") {
         var counter = 0
         permutations {
            iterations = 10
            afterPermutation {
               counter++
            }
            forEach {

            }
         }
         counter shouldBe 10
      }
   }
}
