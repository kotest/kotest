package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.bytes.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTest
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.checkCoverage

class ByteTest : FunSpec({
   test("<Byte, Byte> should give values between min and max inclusive") {
      // Test parameters include the test for negative bounds
      forAll(
         row(-10, -1),
         row(1, 3),
         row(-100, 100),
         row((Byte.MAX_VALUE - 10).toByte(), Byte.MAX_VALUE),
         row(Byte.MIN_VALUE, (Byte.MIN_VALUE + 10).toByte())
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).map { it.toByte() }.toSet()
         val actualValues = (1..100_000).map { Arb.byte(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.byte edgecases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<Byte, Byte> { min, max ->
            if (min < max) {
               classify("run")
               Arb.byte(min, max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})

class ByteArrayTest : DescribeSpec() {
   init {
      describe("Arb.byteArrays") {
         it("should generate specified lengths") {
            Arb.byteArray(Arb.int(5, 15), Arb.byte()).take(1000).toList().forAll {
               it.size.shouldBeGreaterThanOrEqual(5)
               it.size.shouldBeLessThanOrEqual(15)
            }
         }
         it("should populate random byte values") {
            Arb.byteArray(Arb.constant(1000000), Arb.byte()).take(10).toList().forAll {
               it.toSet().size shouldBe 256
            }
         }
      }
   }
}


class UByteTest : FunSpec({
   test("<UByte, UByte> should give values between min and max inclusive") {
      forAll(
         row(1u, 3u),
         row(0u, 100u),
         row((UByte.MAX_VALUE - 10u).toUByte(), UByte.MAX_VALUE),
         row(UByte.MIN_VALUE, (UByte.MIN_VALUE + 10u).toUByte())
      ) { vMin, vMax ->
         val expectedValues = (vMin..vMax).map { it.toUByte() }.toSet()
         val actualValues = (1..100_000).map { Arb.ubyte(vMin, vMax).single() }.toSet()
         actualValues shouldBe expectedValues
      }
   }

   test("Arb.ubyte edgecases should respect min and max bounds") {
      checkCoverage("run", 25.0) {
         PropTest(iterations = 1000).checkAll<UByte, UByte> { min, max ->
            if (min < max) {
               classify("run")
               Arb.ubyte(min, max).edgecases().forAll {
                  it.shouldBeBetween(min, max)
               }
            }
         }
      }
   }
})
