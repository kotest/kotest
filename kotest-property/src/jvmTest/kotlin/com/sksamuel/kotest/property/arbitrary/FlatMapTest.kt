package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

class FlatMapTest : FunSpec() {
   init {
      test("Arb.flatMap should compute probabilistic edgecases") {
         val arbString = Arb.int(1..10).withEdgecases(1, 2).flatMap { a ->
            Arb.double().withEdgecases(1.0, 2.0).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  "$a $b $c"
               }
            }
         }
         val edges = arbString
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(10)
            .map { it.value }
            .toList()

         edges shouldContainExactly listOf(
            "2 2.0 foo",
            "2 2.0 foo",
            "1 1.0 bar",
            "2 1.0 bar",
            "1 2.0 foo",
            "1 2.0 foo",
            "2 2.0 foo",
            "1 1.0 foo",
            "2 2.0 bar",
            "2 1.0 bar"
         )
      }

      test("Arb.flatMap should preserve edgecases if any of edgecases were none") {
         val arbString = Arb.int(1..10).withEdgecases(emptyList()).flatMap { a ->
            Arb.int(11..20).withEdgecases(emptyList()).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  "$a $b $c"
               }
            }
         }
         val edges = arbString
            .generate(RandomSource.seeded(1234L), EdgeConfig(edgecasesGenerationProbability = 1.0))
            .take(10)
            .map { it.value }
            .toList()

         edges shouldContainExactly listOf(
            "10 19 foo",
            "5 20 foo",
            "5 19 bar",
            "1 12 bar",
            "4 13 foo",
            "1 15 foo",
            "1 13 foo",
            "10 12 foo",
            "4 13 bar",
            "8 18 bar"
         )
      }


      test("Arb.flatMap should generate dependent Arb") {
         Arb.int(1..10).flatMap { Arb.int(1..it * it) }.take(15, RandomSource.seeded(3242344L))
            .toList() shouldContainExactly
            listOf(
               16,
               1,
               6,
               23,
               6,
               17,
               3,
               1,
               44,
               24,
               25,
               8,
               2,
               3,
               22
            )
      }
   }
}
