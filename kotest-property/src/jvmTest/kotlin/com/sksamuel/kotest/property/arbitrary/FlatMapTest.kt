package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.*

class FlatMapTest : FunSpec() {
   init {
      test("flat map") {
         Arb.int(1..10).flatMap { listOf(it, it) }.take(15, RandomSource.seeded(3242344L)).toList() shouldBe listOf(
            4,
            4,
            10,
            10,
            5,
            5,
            1,
            1,
            3,
            3,
            8,
            8,
            6,
            6,
            9
         )
      }
   }
}
