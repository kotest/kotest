package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.modifyEdgecases
import io.kotest.property.arbitrary.single
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

      test("should override the initial edges when specified a static EdgeCases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }.withEdgecases { listOf(2, 3) }
         arbInt.edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
         arbInt.edgecases(RandomSource.Default) shouldBe listOf(2, 3)
      }

      test("should override the initial edgecases(rs) when specified a random EdgeCases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
            .withEdgecases { rs -> listOf(Arb.int(1..10).single(rs)) }
         arbInt.edgecases(RandomSource.seeded(1234L)) shouldBe listOf(10)
      }
   }

   context("Arb<A>.modifyEdgecases") {
      test("should modify the initial edgecases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.modifyEdgecases { it + listOf(2, 3) }.edgecases() shouldContainExactlyInAnyOrder listOf(1, 2, 3)
      }

      test("should modify the initial edges") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.modifyEdgecases { edgeCases -> edgeCases + listOf(2, 3)}
            .edgecases() shouldContainExactlyInAnyOrder listOf(1, 2, 3)
      }
   }
})
