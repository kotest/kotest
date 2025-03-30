package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
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
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.positiveInt
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
   }
})

sealed class X {
   data class A(val a: Int) : X()
   data class B(val b: Int) : X()
   data class C(val c: Int) : X()
}
