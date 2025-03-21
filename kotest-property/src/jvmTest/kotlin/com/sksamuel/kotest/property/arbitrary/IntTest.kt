@file:Suppress("DEPRECATION")

package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.*
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTest
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

@EnabledIf(NotMacOnGithubCondition::class)
class IntTest : FunSpec({
   test("<Int, Int> should give values between min and max inclusive") {
      // Test parameters include the test for negative bounds
      forAll(
         row(-10, -1),
         row(1, 3),
         row(-100, 100),
         row(Int.MAX_VALUE - 10, Int.MAX_VALUE),
         row(Int.MIN_VALUE, Int.MIN_VALUE + 10)
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).toSet()
         val actualValues = (1..100_000).map { Arb.int(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.int edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<Int, Int> { min, max ->
            if (min < max) {
               classify("run")
               Arb.int(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }

   test("Arb.positiveInts should return positive ints only") {
      val numbers = Arb.positiveInt().take(1000).toSet()
      numbers.forAll { it.shouldBePositive() }
   }

   test("Arb.nonNegativeInts should return non negative ints only") {
      val numbers = Arb.nonNegativeInt().take(1000).toSet()
      numbers.forAll { it.shouldBeNonNegative() }
   }

   test("Arb.negativeInts should return negative ints only") {
      val numbers = Arb.negativeInt().take(1000).toSet()
      numbers.forAll { it.shouldBeNegative() }
   }

   test("Arb.nonPositiveInts should return non positive ints only") {
      val numbers = Arb.nonPositiveInt().take(1000).toSet()
      numbers.forAll { it.shouldBeNonPositive() }
   }
})

class UIntTest : FunSpec({
   test("<UInt, UInt> should give values between min and max inclusive") {
      forAll(
         row(1u, 3u),
         row(0u, 100u),
         row(UInt.MAX_VALUE - 10u, UInt.MAX_VALUE),
         row(UInt.MIN_VALUE, UInt.MIN_VALUE + 10u)
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).toSet()
         val actualValues = (1..100_000).map { Arb.uInt(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.uInt edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<UInt, UInt> { min, max ->
            if (min < max) {
               classify("run")
               Arb.uInt(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})
