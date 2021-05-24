package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.longs.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.ulong
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

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

   test("Arb.long edgecases should respect min and max bounds") {
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
         val actualValues = (1..100_000).map { Arb.ulong(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.ulong edgecases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         checkAll<ULong, ULong> { min, max ->
            if (min < max) {
               classify("run")
               Arb.ulong(min..max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})
