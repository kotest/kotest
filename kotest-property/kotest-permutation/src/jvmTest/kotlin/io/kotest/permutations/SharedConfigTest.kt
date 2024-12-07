package io.kotest.permutations

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SharedConfigTest : FunSpec() {
   init {
      test("shared config should be used when passed to the permutation function") {

         val context = permutationConfiguration {
            iterations = 42 // run this property test 42 times
         }

         permutations(context) {
            forEach {  }
         }.iterations shouldBe 42

         permutations(context) {
            forEach {  }
         }.iterations shouldBe 42
      }
   }
}
