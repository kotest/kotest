package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.modifyEdgecases
import io.kotest.property.arbitrary.withEdgecases

class WithEdgecasesTest : FunSpec({
   context("Arb<A>.withEdgecases") {
      test("should override the initial edgecases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgecases(2, 3).edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }

      test("should override the initial edgecases when specified a list") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgecases(listOf(2, 3)).edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }
   }

   context("Arb<A>.modifyEdgecases") {
      test("should modify the initial edgecases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.modifyEdgecases { it + listOf(2, 3) }.edgecases() shouldContainExactlyInAnyOrder listOf(1, 2, 3)
      }
   }
})
