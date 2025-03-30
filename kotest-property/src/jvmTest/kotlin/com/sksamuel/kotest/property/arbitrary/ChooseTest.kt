package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.random

@EnabledIf(LinuxOnlyGithubCondition::class)
class ChooseTest : FunSpec({

   test("Arb.choose should honour seed") {
      val seedListA =
         Arb.choose(1 to 'A', 3 to 'B', 4 to 'C', 5 to 'D').samples(684658365846L.random()).take(500).toList()
            .map { it.value }
      val seedListB =
         Arb.choose(1 to 'A', 3 to 'B', 4 to 'C', 5 to 'D').samples(684658365846L.random()).take(500).toList()
            .map { it.value }
      seedListA shouldBe seedListB
   }

   test("Arb.choose for values should generate expected values in correct ratios according to weights") {
      forAll(
         row(listOf(1 to 'A', 1 to 'B'), mapOf('A' to 0.5, 'B' to 0.5)),
         row(listOf(1 to 'A', 3 to 'B', 1 to 'C'), mapOf('A' to 0.2, 'B' to 0.6, 'C' to 0.2)),
         row(listOf(1 to 'A', 3 to 'C', 1 to 'C'), mapOf('A' to 0.2, 'C' to 0.8)),
         row(listOf(1 to 'A', 3 to 'B', 1 to 'C', 4 to 'D'), mapOf('A' to 0.11, 'B' to 0.33, 'C' to 0.11, 'D' to 0.44))
      ) { weightPairs, expectedRatiosMap ->
         val genCount = 100000
         val chooseGen = Arb.choose(weightPairs[0], weightPairs[1], *weightPairs.drop(2).toTypedArray())
         val actualCountsMap = (1..genCount).map { chooseGen.single() }.groupBy { it }.map { (k, v) -> k to v.count() }
         val actualRatiosMap = actualCountsMap.associate { (k, v) -> k to (v.toDouble() / genCount) }

         actualRatiosMap.keys shouldBe expectedRatiosMap.keys

         actualRatiosMap.forEach { (k, actualRatio) ->
            actualRatio shouldBe (expectedRatiosMap[k] as Double plusOrMinus 0.02)
         }
      }
   }

   test("Arb.choose should not accept negative weights") {
      shouldThrow<IllegalArgumentException> { Arb.choose(-1 to 'A', 1 to 'B') }
   }

   test("Arb.choose should not accept all zero weights") {
      shouldThrow<IllegalArgumentException> { Arb.choose(0 to 'A', 0 to 'B') }
   }

   test("Arb.choose should accept weights if at least one is non-zero") {
      shouldNotThrow<Exception> { Arb.choose(0 to 'A', 0 to 'B', 1 to 'C') }
   }

   test("Arb.choose(arbs) should generate expected values in correct ratios according to weights") {
      val arbA = Arb.constant('A')
      val arbB = Arb.constant('B')
      val arbC = Arb.constant('C')
      val arbD = Arb.constant('D')
      forAll(
         row(listOf(1 to arbA, 1 to arbB), mapOf('A' to 0.5, 'B' to 0.5)),
         row(listOf(1 to arbA, 3 to arbB, 1 to arbC), mapOf('A' to 0.2, 'B' to 0.6, 'C' to 0.2)),
         row(listOf(1 to arbA, 3 to arbC, 1 to arbC), mapOf('A' to 0.2, 'C' to 0.8)),
         row(
            listOf(1 to arbA, 3 to arbB, 1 to arbC, 4 to arbD),
            mapOf('A' to 0.11, 'B' to 0.33, 'C' to 0.11, 'D' to 0.44)
         )
      ) { weightPairs, expectedRatiosMap ->
         val genCount = 100000
         val chooseGen = Arb.choose(weightPairs[0], weightPairs[1], *weightPairs.drop(2).toTypedArray())
         val actualCountsMap = (1..genCount).map { chooseGen.single() }.groupBy { it }.map { (k, v) -> k to v.count() }
         val actualRatiosMap = actualCountsMap.associate { (k, v) -> k to (v.toDouble() / genCount) }

         actualRatiosMap.keys shouldBe expectedRatiosMap.keys

         actualRatiosMap.forEach { (k, actualRatio) ->
            actualRatio shouldBe (expectedRatiosMap[k] as Double plusOrMinus 0.02)
         }
      }
   }

   test("Arb.choose(arbs) should not accept all zero weights") {
      shouldThrow<IllegalArgumentException> { Arb.choose(0 to Arb.constant('A'), 0 to Arb.constant('B')) }
   }

   test("Arb.choose(arbs) should not accept negative weights") {
      shouldThrow<IllegalArgumentException> { Arb.choose(-1 to Arb.constant('A'), 1 to Arb.constant('B')) }
   }

   test("Arb.choose(arbs) should accept weights if at least one is non-zero") {
      shouldNotThrow<Exception> { Arb.choose(0 to Arb.constant('A'), 0 to Arb.constant('B'), 1 to Arb.constant('C')) }
   }

   test("Arb.choose(arbs) should collate edge cases") {
      val arb = Arb.choose(
         1 to Arb.constant('A').withEdgecases('a'),
         3 to Arb.constant('B').withEdgecases('b'),
         4 to Arb.constant('C').withEdgecases('c'),
         5 to Arb.constant('D').withEdgecases('d')
      )
      val edgeCases = arb
         .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
         .take(10)
         .map { it.value }
         .toList()

      edgeCases shouldContainExactly listOf(
         'c',
         'c',
         'd',
         'a',
         'b',
         'a',
         'd',
         'd',
         'a',
         'b'
      )
   }
})
