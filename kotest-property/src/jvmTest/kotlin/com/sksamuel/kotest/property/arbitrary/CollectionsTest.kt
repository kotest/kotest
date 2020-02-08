package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.single
import io.kotest.property.forAll

class CollectionsTest : FunSpec({

   test("gen list should not include empty edgecases as first list") {
      val numGen = Arb.list(Arb.create { it.random.nextInt() }, 1..100)
      forAll(1, numGen) { b ->
         b.isNotEmpty()
      }
   }

   test("gen list should return lists of underlying generators") {
      val gen = Arb.list(Exhaustive.single(1), 2..10)
      checkAll(gen) {
         it.shouldHaveAtLeastSize(2)
         it.shouldHaveAtMostSize(10)
         it.toSet() shouldBe setOf(1)
      }
   }
})
