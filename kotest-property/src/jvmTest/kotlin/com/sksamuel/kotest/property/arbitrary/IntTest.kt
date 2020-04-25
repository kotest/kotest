package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTest
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

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
         val actualValues = (1..100000).map { Arb.int(vMin, vMax).single() }.toSet()

         actualValues shouldBe expectedValues
      }
   }

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
         val actualValues = (1..100000).map { Arb.long(vMin, vMax).single() }.toSet()

         actualValues shouldBe expectedValues
      }
   }

   test("edgecases should respect min and max bounds") {
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
})
