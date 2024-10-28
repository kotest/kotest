package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.longs.shouldBeNegative
import io.kotest.matchers.longs.shouldBeNonNegative
import io.kotest.matchers.longs.shouldBeNonPositive
import io.kotest.matchers.longs.shouldBePositive
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.negativeLong
import io.kotest.property.arbitrary.nonNegativeLong
import io.kotest.property.arbitrary.nonPositiveLong
import io.kotest.property.arbitrary.positiveLong
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.uLong
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

@EnabledIf(LinuxCondition::class)
class LongTest : FunSpec({
   test("<Long, Long> should give values between min and max inclusive") {
      // Test parameters include the test for negative bounds
      forAll(
         row(-10L, -1L),
         row(1L, 3L),
         row(-100L, 100L),
         row(Long.MAX_VALUE - 10L, Long.MAX_VALUE),
         row(Long.MIN_VALUE, Long.MIN_VALUE + 10L)
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).toSet()
         val actualValues = (1..100_000).map { Arb.long(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.long edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         checkAll<Long, Long> { min, max ->
            if (min < max) {
               classify("run")
               Arb.long(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }

   test("Arb.long generates values in range") {
      val min = -140_737_488_355_328 // -2^47
      val max = 140_737_488_355_327 // 2^47 - 1
      checkAll(100_000, Arb.long(min, max)) { value ->
         value.shouldBeBetween(min, max)
      }
   }

   test("Arb.positiveLongs should return positive longs only") {
      val numbers = Arb.positiveLong().take(1000).toSet()
      numbers.forAll { it.shouldBePositive() }
   }

   test("Arb.nonNegativeLongs should return non negative longs only") {
      val numbers = Arb.nonNegativeLong().take(1000).toSet()
      numbers.forAll { it.shouldBeNonNegative() }
   }

   test("Arb.negativeLongs should return negative longs only") {
      val numbers = Arb.negativeLong().take(1000).toSet()
      numbers.forAll { it.shouldBeNegative() }
   }

   test("Arb.nonPositiveLongs should return non positive longs only") {
      val numbers = Arb.nonPositiveLong().take(1000).toSet()
      numbers.forAll { it.shouldBeNonPositive() }
   }
})

class ULongTest : FunSpec({
   test("<ULong, ULong> should give values between min and max inclusive") {
      forAll(
         row(1uL, 3uL),
         row(0uL, 100uL),
         row(ULong.MAX_VALUE - 10uL, ULong.MAX_VALUE),
         row(ULong.MIN_VALUE, ULong.MIN_VALUE + 10uL)
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).toSet()
         val actualValues = (1..100_000).map { Arb.uLong(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.uLong edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         checkAll<ULong, ULong> { min, max ->
            if (min < max) {
               classify("run")
               Arb.uLong(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }

   test("Arb.uLong generates values in range") {
      val min = 0uL
      val max = 281_474_976_710_655u // 2^48 - 1
      checkAll(100_000, Arb.uLong(min, max)) { value ->
         value.shouldBeBetween(min, max)
      }
   }
})
