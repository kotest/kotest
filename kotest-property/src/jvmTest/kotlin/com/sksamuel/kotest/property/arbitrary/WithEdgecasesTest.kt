package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.EdgeCases
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.modifyEdgecases
import io.kotest.property.arbitrary.modifyEdges
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.arbitrary.withEdges

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
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }.withEdges(EdgeCases.of(2, 3))
         arbInt.edgecases() shouldContainExactlyInAnyOrder listOf(2, 3)
         arbInt.edges().values(RandomSource.seeded(1234L)) shouldBe listOf(2, 3)
      }

      test("should override the initial edges when specified a random EdgeCases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }.withEdges(EdgeCases.random { 3 })
         arbInt.edgecases().shouldBeEmpty()
         arbInt.edges().shouldBeTypeOf<EdgeCases.Random<Int>>()
         arbInt.edges().values(RandomSource.seeded(1234L)) shouldBe listOf(3)
      }
   }

   context("Arb<A>.modifyEdgecases") {
      test("should modify the initial edgecases") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.modifyEdgecases { it + listOf(2, 3) }.edgecases() shouldContainExactlyInAnyOrder listOf(1, 2, 3)
      }

      test("should modify the initial edges") {
         val arbInt = arbitrary(listOf(1)) { it.random.nextInt() }
         arbInt.modifyEdges { edgeCases -> edgeCases + EdgeCases.of(2, 3) }
            .edgecases() shouldContainExactlyInAnyOrder listOf(1, 2, 3)
      }
   }
})
