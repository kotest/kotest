package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.edgeCases
import io.kotest.property.arbitrary.modifyEdgeCases
import io.kotest.property.arbitrary.withEdgeCases

class WithEdgecasesTest : FunSpec({
   context("Arb<A>.withEdgecases") {
      test("should override the initial edge cases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgeCases(2, 3).edgeCases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }

      test("should override the initial edge cases when specified a list") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgeCases(listOf(2, 3)).edgeCases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }
   }

   context("Arb<A>.modifyEdgecases") {
      test("should modify the each edge case") {
         val arbInt = arbitrary(listOf(1, 2, 3)) { it.random.nextInt() }
         arbInt.modifyEdgeCases { it * 2 }.edgeCases() shouldContainExactlyInAnyOrder listOf(2, 4, 6)
      }
   }
})
