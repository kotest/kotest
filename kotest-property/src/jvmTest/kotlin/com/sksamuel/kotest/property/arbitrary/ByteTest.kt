package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.matchers.bytes.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.take

class ByteTest : DescribeSpec() {
   init {
      describe("Arb.byte") {
         it("should respect min / max") {
            Arb.byte(5.toByte(), 9.toByte()).take(1000).forAll {
               it.shouldBeBetween(5, 9)
            }
         }
         it("should only include 0 in the edge cases if within the bounds") {
            Arb.byte(5.toByte(), 9.toByte()).edgecases().forNone { it shouldBe 0.toByte() }
            Arb.byte(0.toByte(), 9.toByte()).edgecases().forOne { it shouldBe 0.toByte() }
            Arb.byte((-5).toByte(), 0.toByte()).edgecases().forOne { it shouldBe 0.toByte() }
         }
         it("should only include 1 in the edge cases if within the bounds") {
            Arb.byte(5.toByte(), 9.toByte()).edgecases().forNone { it shouldBe 1.toByte() }
            Arb.byte(0.toByte(), 9.toByte()).edgecases().forOne { it shouldBe 1.toByte() }
            Arb.byte(1.toByte(), 9.toByte()).edgecases().forOne { it shouldBe 1.toByte() }
            Arb.byte(1.toByte(), 1.toByte()).edgecases().forOne { it shouldBe 1.toByte() }
            Arb.byte((-5).toByte(), 1.toByte()).edgecases().forOne { it shouldBe 1.toByte() }
            Arb.byte((-5).toByte(), 2.toByte()).edgecases().forOne { it shouldBe 1.toByte() }
         }
         it("should only include -1 in the edge cases if within the bounds") {
            Arb.byte(5.toByte(), 9.toByte()).edgecases().forNone { it shouldBe (-1).toByte() }
            Arb.byte(0.toByte(), 9.toByte()).edgecases().forNone { it shouldBe (-1).toByte() }
            Arb.byte((-1).toByte(), 9.toByte()).edgecases().forOne { it shouldBe (-1).toByte() }
            Arb.byte((-1).toByte(), (-1).toByte()).edgecases().forOne { it shouldBe (-1).toByte() }
            Arb.byte((-2).toByte(), 0.toByte()).edgecases().forOne { it shouldBe (-1).toByte() }
            Arb.byte((-5).toByte(), 0.toByte()).edgecases().forOne { it shouldBe (-1).toByte() }
            Arb.byte((-5).toByte(), (-1).toByte()).edgecases().forOne { it shouldBe (-1).toByte() }
         }
      }
   }
}
