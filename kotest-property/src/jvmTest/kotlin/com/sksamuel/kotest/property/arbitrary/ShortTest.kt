package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.short.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTest
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

@EnabledIf(LinuxCondition::class)
class ShortTest : FunSpec({
   test("<Short, Short> should give values between min and max inclusive") {
      // Test parameters include the test for negative bounds
      forAll(
         row(-10, -1),
         row(1, 3),
         row(-100, 100),
         row((Short.MAX_VALUE - 10).toShort(), Short.MAX_VALUE),
         row(Short.MIN_VALUE, (Short.MIN_VALUE + 10).toShort())
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).map { it.toShort() }.toSet()
         val actualValues = (1..100_000).map { Arb.short(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.short edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<Short, Short> { min, max ->
            if (min < max) {
               classify("run")
               Arb.short(min, max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})

class UShortTest : FunSpec({
   test("<UShort, UShort> should give values between min and max inclusive") {
      forAll(
         row(1u, 3u),
         row(0u, 100u),
         row((UShort.MAX_VALUE - 10u).toUShort(), UShort.MAX_VALUE),
         row(UShort.MIN_VALUE, (UShort.MIN_VALUE + 10u).toUShort())
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).map { it.toUShort() }.toSet()
         val actualValues = (1..100_000).map { Arb.uShort(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.uShort edge cases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<UShort, UShort> { min, max ->
            if (min < max) {
               classify("run")
               Arb.uShort(min, max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})


