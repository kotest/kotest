package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArrays
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take

class ByteArrayTest : DescribeSpec() {
   init {
      describe("Arb.byteArrays") {
         it("should generate specified lengths") {
            Arb.byteArrays(Arb.int(5, 15), Arb.byte()).take(1000).toList().forAll {
               it.size.shouldBeGreaterThanOrEqual(5)
               it.size.shouldBeLessThanOrEqual(15)
            }
         }
         it("should populate random byte values") {
            Arb.byteArrays(Arb.constant(1000000), Arb.byte()).take(10).toList().forAll {
               it.toSet().size shouldBe 256
            }
         }
      }
   }
}
