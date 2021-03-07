package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.take
import io.kotest.property.forAll

class ChoiceTest : WordSpec({

   "Arb.choice" should {
      "correctly handle multiple generators" {
         val gen = Arb.choice(Arb.positiveInts(), Arb.negativeInts())
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
         val arbs: Arb<X> = Arb.choice(
            Arb.int().map { X.A(it) },
            Arb.int().map { X.B(it) },
            Arb.int().map { X.C(it) }
         )
      }
      "combines the provided Arb instances edgecases" {
         val arbs = Arb.choice(
            arbitrary(listOf(1, 2)) { 5 },
            arbitrary(listOf(3, 4)) { 6 }
         )
         val rs = RandomSource.seeded(1234L)
         val edgecases = generateSequence { arbs.generateEdgecase(rs) }.take(10).toList()
         edgecases shouldContainExactly listOf(
            2,
            4,
            3,
            4,
            4,
            1,
            1,
            2,
            1,
            3
         )
      }
      "provides both edgecases and values when used as a Gen" {
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
      "edgecases should not be in Arb.samples" {
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
