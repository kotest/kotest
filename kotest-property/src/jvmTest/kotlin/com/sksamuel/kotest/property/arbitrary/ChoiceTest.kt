package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.positiveInts
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
         Arb.choice(
            arb(listOf(1, 2)) { emptySequence() },
            arb(listOf(3, 4)) { emptySequence() }
         ).edgecases() shouldBe listOf(1, 2, 3, 4)
      }
      "provides both edgecases and values when used as a Gen" {
         val values = mutableSetOf<Int>()
         forAll(
            Arb.choice(
               arb(listOf(1)) { generateSequence { 2 } },
               arb(listOf(3)) { generateSequence { 4 } }
            )
         ) { i ->
            values.add(i)
            listOf(1, 2, 3, 4).contains(i)
         }
         values shouldBe setOf(1, 2, 3, 4)
      }
      "edgecases should not be in Arb.values" {
         val valueSet = Arb.choice(
            arb(listOf(-1)) { generateSequence { 1 } },
            arb(listOf(-2)) { generateSequence { 2 } }
         )
            .values(RandomSource.Default)
            .map(Sample<Int>::value)
            .take(1000)
            .toSet()

         valueSet shouldBe setOf(1, 2)
      }
      "Arguments must be passed to Arb.choice" {
         assertSoftly {
            val ex = shouldThrowExactly<IllegalArgumentException> {
               Arb.choice(*arrayOf<Arb<Int>>()).next()
            }
            ex.message shouldBe "No Arb instances passed to Arb.choice()."
         }
      }
   }
})

sealed class X {
   data class A(val a: Int) : X()
   data class B(val b: Int) : X()
   data class C(val c: Int) : X()
}
