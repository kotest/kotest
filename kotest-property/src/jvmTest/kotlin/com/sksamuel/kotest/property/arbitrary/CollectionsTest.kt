package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.constant
import io.kotest.property.forAll

class CollectionsTest : FunSpec({

   test("Arb.list should not include empty edgecases as first sample") {
      val numGen = Arb.list(Arb.int(), 1..10)
      forAll(1, numGen) { it.isNotEmpty() }
   }

   test("Arb.set should not include empty edgecases as first sample") {
      val numGen = Arb.set(Arb.int(), 1..10)
      forAll(1, numGen) { it.isNotEmpty() }
   }

   test("Arb.list should return lists of underlying generators") {
      val gen = Arb.list(Exhaustive.constant(1), 2..10)
      checkAll(gen) {
         it.shouldHaveAtLeastSize(2)
         it.shouldHaveAtMostSize(10)
         it.toSet() shouldBe setOf(1)
      }
   }

   test("Arb.list should generate lists of length up to 100 by default") {
      checkAll(10_000, Arb.list(Arb.double())) {
         it.shouldHaveAtMostSize(100)
      }
   }

   test("Arb.list should generate lists in the given range") {
      checkAll(1000, Arb.list(Arb.double(), 250..500)) {
         it.shouldHaveAtLeastSize(250)
         it.shouldHaveAtMostSize(500)
      }
   }

   test("Arb.list should generate sets of length up to 100 by default") {
      checkAll(10_000, Arb.set(Arb.double())) {
         it.shouldHaveAtMostSize(100)
      }
   }

   test("Arb.list should generate sets in the given range") {
      checkAll(1000, Arb.set(Arb.double(), 250..500)) {
         it.shouldHaveAtLeastSize(250)
         it.shouldHaveAtMostSize(500)
      }
   }
})
