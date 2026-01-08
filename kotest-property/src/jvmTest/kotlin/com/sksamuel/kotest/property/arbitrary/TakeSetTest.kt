package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.takeSet

class TakeSetTest: StringSpec() {
   init {
       "takes set shorter than required because not enough elements" {
          takeSet(
          sequenceOf(1, 2, 3),
             targetSize = 4,
             maxMisses = 20,
          ) shouldBe setOf(1, 2, 3)
       }
      "takes set shorter than required because not enough distinct elements" {
         takeSet(
            sequenceOf(1, 2, 3, 1, 2, 3, 1, 2, 3,),
            targetSize = 4,
            maxMisses = 20,
         ) shouldBe setOf(1, 2, 3)
      }
      "takes set shorter than required because not enough distinct elements within slippage" {
         takeSet(
            sequenceOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 4,),
            targetSize = 4,
            maxMisses = 8,
         ) shouldBe setOf(1, 2, 3)
      }
      "takes set of required size" {
         takeSet(
            sequenceOf(1, 2, 1, 2, 1, 2, 3, 4,),
            targetSize = 4,
            maxMisses = 20,
         ) shouldBe setOf(1, 2, 3, 4)
      }
   }
}
