package io.kotest.property.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SharedConfigTest : FunSpec() {
   init {
      test("shared config should be used") {

         val context = permutationConfiguration {
            iterations = 42 // run this property test 42 times
         }

         permutations {
            forEach {  }
         }.iterations shouldBe 42

         permutations {
            forEach {  }
         }.iterations shouldBe 42
      }
   }
}
