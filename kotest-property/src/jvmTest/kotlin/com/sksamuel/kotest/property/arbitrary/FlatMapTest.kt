package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.take

class FlatMapTest : FunSpec() {
   init {
      test("Arb.flatMap") {
         Arb.int(1..10).flatMap { Arb.int(1..it * it) }.take(15, RandomSource.seeded(3242344L))
            .toList() shouldContainExactly
            listOf(
               9,
               1,
               33,
               15,
               1,
               13,
               15,
               28,
               1,
               44,
               7,
               23,
               19,
               24,
               1
            )
      }
   }
}
