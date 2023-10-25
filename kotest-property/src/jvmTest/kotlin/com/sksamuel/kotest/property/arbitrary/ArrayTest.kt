package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.array
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.constant
import io.kotest.property.forAll

class ArrayTest : DescribeSpec({
   describe("ByteArray should") {
      it("generate specified lengths") {
         Arb.byteArray(Arb.int(5, 15), Arb.byte()).take(1000).toList().forAll {
            it.size.shouldBeGreaterThanOrEqual(5)
            it.size.shouldBeLessThanOrEqual(15)
         }
      }

      it("populate random byte values") {
         Arb.byteArray(Arb.constant(1000000), Arb.byte()).take(10).toList().forAll {
            it.toSet().size shouldBe 256
         }
      }
   }

   describe("Arb.array should") {
      it("not include empty edge cases as first sample") {
         val numGen = Arb.array(Arb.int(), 1..10)
         forAll(1, numGen) { it.isNotEmpty() }
      }

      it("return arrays of underlying generators") {
         val gen = Arb.array(Exhaustive.constant(1), 2..10)
         checkAll(gen) {
            it.shouldHaveAtLeastSize(2)
            it.shouldHaveAtMostSize(10)
            it.toSet() shouldBe setOf(1)
         }
      }

      it("include repeated elements in edge cases") {
         val edgeCase = Arb.positiveInt().edgecases().firstOrNull()
         Arb.array(Arb.positiveInt()).edgecases() shouldContain listOf(edgeCase, edgeCase)
         Arb.array(Arb.positiveInt(), 4..6).edgecases() shouldContain listOf(edgeCase, edgeCase, edgeCase, edgeCase)
      }

      it("include empty array in edge cases") {
         Arb.array(Arb.positiveInt()).edgecases() shouldContain emptyArray()
      }

      it("respect bounds in edge cases") {
         val edges = Arb.array(Arb.positiveInt(), 2..10).edgecases().toSet()
         edges.forAll { it.shouldNotBeEmpty() }
      }

      it("generate arrays of length up to 100 by default") {
         checkAll(10_000, Arb.array(Arb.double())) {
            it.shouldHaveAtMostSize(100)
         }

         checkAll<Array<Double>>(PropTestConfig(iterations = 10_000)) {
            it.shouldHaveAtMostSize(100)
         }

         forAll<Array<Double>>(PropTestConfig(iterations = 10_000)) {
            it.size <= 100
         }
      }

      it("generate arrays in the given range") {
         checkAll(1000, Arb.array(Arb.double(), 250..500)) {
            it.shouldHaveAtLeastSize(250)
            it.shouldHaveAtMostSize(500)
         }
      }
   }
})
