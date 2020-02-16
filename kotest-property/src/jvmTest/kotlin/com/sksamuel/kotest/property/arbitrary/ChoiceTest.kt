package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInts
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.forAll

class ChoiceTest : WordSpec({

   "Gen.oneOf" should {
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
         positiveNumbers shouldBe beGreaterThan(1)
         negativeNumbers shouldBe beGreaterThan(1)
      }
      "support covariance" {
         Arb.choice(
            Arb.bind(Arb.int(), X::A),
            Arb.bind(Arb.int(), X::B),
            Arb.bind(Arb.int(), X::C)
         )
      }
   }
})

sealed class X {
   data class A(val a: Int) : X()
   data class B(val b: Int) : X()
   data class C(val c: Int) : X()
}
