package io.kotest.property.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SharedConfigTest : FunSpec() {
   init {
      test("shared config should be used") {

         val context = permutationContext {
            iterations = 42 // run this property test 42 times
         }

         permutations {
            forEach {  }
         }.evaluations shouldBe 42

         permutations {
            forEach {  }
         }.evaluations shouldBe 42
      }
   }
}
