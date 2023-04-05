package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.permutations

class PermutationsTest : FunSpec() {
   init {
      test("Exhaustive.permutations") {
         Exhaustive.permutations(listOf(1, 2, 3)).values shouldBe listOf(
            listOf(3, 2, 1), listOf(2, 3, 1), listOf(3, 1, 2), listOf(1, 3, 2), listOf(2, 1, 3), listOf(1, 2, 3)
         )
      }
   }
}
