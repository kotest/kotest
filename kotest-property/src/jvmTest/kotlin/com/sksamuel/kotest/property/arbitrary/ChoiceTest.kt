package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.edgecase
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.take
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class ChoiceTest : WordSpec({

   "Arb.choice" should {
      "correctly handle multiple generators" {
         val gen = Arb.choice(Arb.positiveInt(), Arb.negativeInt())
         var positiveNumbers = 0
         var negativeNumbers = 0
         forAll(gen) {
            if (it > 0) {
               positiveNumbers++
            } else if (it < 0) {
               negativeNumbers++
            }
            it shouldNotBe 0
            true
         }
         negativeNumbers shouldBe beGreaterThan(1)
         positiveNumbers shouldBe beGreaterThan(1)
      }
      "support covariance" {
         Arb.choice(
            Arb.int().map { X.A(it) },
            Arb.int().map { X.B(it) },
            Arb.int().map { X.C(it) }
         )
      }
      "combines the provided Arb instances edge cases" {
         val arb = Arb.choice(
            arbitrary(listOf(1, 2)) { 5 },
            arbitrary(listOf(3, 4)) { 6 }
         )
         val edgeCases = arb
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(10)
            .map { it.value }
            .toList()
         edgeCases shouldContainExactly listOf(
            2,
            2,
            3,
            4,
            1,
            1,
            4,
            3,
            1,
            3
         )
      }
      "provides both edge cases and values when used as a Gen" {
         val values = mutableSetOf<Int>()
         forAll(
            Arb.choice(
               arbitrary(listOf(1)) { 2 },
               arbitrary(listOf(3)) { 4 }
            )
         ) { i ->
            values.add(i)
            listOf(1, 2, 3, 4).contains(i)
         }
         values shouldBe setOf(1, 2, 3, 4)
      }
      "finds the only arb with edge cases when most have none" {
         val arbWithEdge = arbitrary(listOf(42)) { 0 }
         val arbWithoutEdge = arbitrary { 0 }.removeEdgecases()
         val arbList = listOf(arbWithoutEdge, arbWithoutEdge, arbWithEdge, arbWithoutEdge)
         repeat(20) { seed ->
            arbList.edgecase(RandomSource.seeded(seed.toLong())) shouldNotBe null
         }
      }
      "edge cases should not be in Arb.samples" {
         val valueSet = Arb
            .choice(
               arbitrary(listOf(-1)) { 1 },
               arbitrary(listOf(-2)) { 2 }
            )
            .take(1000)
            .toSet()

         valueSet shouldBe setOf(1, 2)
      }
      // regression for a bug where List<Arb<A>>.edgecase shuffled the list on every recursive
      // call but dropped the head of the *unshuffled* list, so arbs whose edges were the only
      // ones available could be discarded before being visited.
      "find an edge case even when most arbs have no edge cases" {
         // Single arb with an edge case mixed with many arbs that have none. removeEdgecases
         // is essential here because plain `arbitrary { ... }` falls back to its sample value
         // for edgecase(), which would mask the bug.
         val withEdge = arbitrary(listOf(42)) { 99 }
         val withoutEdge: List<Arb<Int>> = List(20) { arbitrary { 0 }.removeEdgecases() }
         // withEdge is placed at the front - the buggy implementation dropped the head of the
         // unshuffled list on every recursion, so the only arb with edges was discarded almost
         // immediately. Placing it at the end would mask the bug because it would survive all
         // the drops and end up tested last.
         val arbs: List<Arb<Int>> = listOf(withEdge) + withoutEdge

         // Try many seeds - on the buggy implementation this returned null for the majority of
         // seeds, since the only-arb-with-an-edge typically ended up at an index > 0 in the
         // original list and got dropped before being inspected. With the fix every seed should
         // surface edge case 42.
         repeat(50) { seed ->
            val sample = arbs.edgecase(RandomSource.seeded(seed.toLong()))
            sample?.value shouldBe 42
         }
      }
   }
})

sealed class X {
   data class A(val a: Int) : X()
   data class B(val b: Int) : X()
   data class C(val c: Int) : X()
}
