package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Arb
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
      test("Arb.flatMap should compute the cartesian product of each arb's edgecases") {
         val arbPair = Arb.int(1..10).withEdgecases(1, 2).flatMap { a ->
            Arb.double().withEdgecases(1.0, 2.0).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  a to b to c
               }
            }
         }

         arbPair.edgecases() shouldContainExactlyInAnyOrder listOf(
            1 to 1.0 to "foo",
            1 to 1.0 to "bar",
            1 to 2.0 to "foo",
            1 to 2.0 to "bar",
            2 to 1.0 to "foo",
            2 to 1.0 to "bar",
            2 to 2.0 to "foo",
            2 to 2.0 to "bar",
         )
      }

      test("Arb.flatMap should compute probabilistic edgecases") {
         val arbString = Arb.int(1..10).withEdgecases(1, 2).flatMap { a ->
            Arb.double().withEdgecases(1.0, 2.0).flatMap { b ->
               Arb.string().withEdgecases("foo", "bar").map { c ->
                  "$a $b $c"
               }
            }
         }
         val rs = RandomSource.seeded(1234L)
         val edges = generateSequence { arbString.generateEdgecase(rs) }.take(10).toList()
         edges shouldContainExactly listOf(
            "1 2.0 bar",
            "2 2.0 foo",
            "2 2.0 bar",
            "2 1.0 foo",
            "1 1.0 foo",
            "2 1.0 foo",
            "2 1.0 bar",
            "1 2.0 foo",
            "2 1.0 foo",
            "2 1.0 bar"
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
