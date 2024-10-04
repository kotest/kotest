package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.sequence
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.constant
import io.kotest.property.forAll

@EnabledIf(LinuxCondition::class)
class SequencesTest : DescribeSpec({
   describe("Arb.sequence should") {

      it("not include empty edge cases as first sample") {
         val numGen = Arb.sequence(Arb.int(), 1..10)
         forAll(1, numGen) { it.any() }
      }

      it("return sequences of underlying generators") {
         val gen = Arb.sequence(Exhaustive.constant(1), 2..10)
         checkAll(gen) {
            it.count() shouldBeGreaterThanOrEqual 2
            it.count() shouldBeLessThanOrEqual 10
            it.toSet() shouldBe setOf(1)
         }
      }

      it("include repeated elements in edge cases") {
         val edgeCase = Arb.positiveInt().edgecases().firstOrNull()
         val a = Arb.sequence(Arb.positiveInt()).edgecases()
         Arb.sequence(Arb.positiveInt()).edgecases().map { it.toList() } shouldContain listOf(edgeCase, edgeCase)
         Arb.sequence(Arb.positiveInt(), 4..6).edgecases().map { it.toList() } shouldContain listOf(
            edgeCase,
            edgeCase,
            edgeCase,
            edgeCase
         )
      }

      it("include empty list in edge cases") {
         Arb.sequence(Arb.positiveInt()).edgecases().map { it.toList() } shouldContain emptyList()
      }

      it("respect bounds in edge cases") {
         val edges = Arb.sequence(Arb.positiveInt(), 2..10).edgecases().toSet()
         edges.forAll { it.count() shouldNotBe 0 }
      }

      it("generate sequences of length up to 100 by default") {
         checkAll(10_000, Arb.sequence(Arb.double())) {
            it.count() shouldBeLessThanOrEqual 100
         }
      }

      it("generate sequences in the given range") {
         checkAll(1000, Arb.sequence(Arb.double(), 250..500)) {
            it.count() shouldBeGreaterThanOrEqual 250
            it.count() shouldBeLessThanOrEqual 500
         }
      }
   }
})
