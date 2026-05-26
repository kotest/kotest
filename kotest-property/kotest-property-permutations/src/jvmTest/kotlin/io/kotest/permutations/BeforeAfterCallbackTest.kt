@file:Suppress("RETURN_VALUE_NOT_USED_COERCION")

package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalKotest::class)
class BeforeAfterCallbackTest : FunSpec() {
   init {
      test("before should be called for each permutation") {
         var counter = 0
         permutations {
            iterations = 10
            before {
               counter++
            }
            check {

            }
         }
         counter shouldBe 10
      }

      test("after should be called for each permutation") {
         var counter = 0
         permutations {
            iterations = 10
            after {
               counter++
            }
            check {

            }
         }
         counter shouldBe 10
      }
   }
}
