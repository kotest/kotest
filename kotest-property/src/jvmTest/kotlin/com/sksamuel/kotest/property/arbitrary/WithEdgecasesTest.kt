package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.modifyEdgecases
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.asSample

@EnabledIf(NotMacOnGithubCondition::class)
class WithEdgecasesTest : FunSpec({
   context("Arb<A>.withEdgecases") {
      test("should override the initial edge cases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgecases(2, 3).edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }

      test("should override the initial edge cases when specified a list") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.withEdgecases(listOf(2, 3)).edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
      }
   }

   context("Arb<A>.modifyEdgecases") {
      test("should modify the each edge case") {
         val arbInt = arbitrary(listOf(1, 2, 3)) { it.random.nextInt() }
         arbInt.modifyEdgecases { (it.value * 2).asSample() }.edgecases() shouldContainExactlyInAnyOrder listOf(2, 4, 6)
      }
   }
})
