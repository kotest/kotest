package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
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

      test("Arb.flatMap should compute cartesian product of each arb's edges") {

         data class Container(val a: Int, val b: Double, val c: Long)

         val arbContainer: Arb<Container> = Arb.int(1..10).withEdgecases(1, 2).flatMap { a ->
            Arb.double().withEdgecases(1.0, 2.0).flatMap { b ->
               Arb.long().withEdgecases().map { c ->
                  Container(a, b, c)
               }
            }
         }

         val allEdges = arbContainer.edges().values(RandomSource.seeded(112314L))
         allEdges shouldContainExactly listOf(
            Container(a = 1, b = 1.0, c = -1887002662852004761),
            Container(a = 1, b = 2.0, c = 7648245381918712633),
            Container(a = 2, b = 1.0, c = -7231280152597965526),
            Container(a = 2, b = 2.0, c = -7225267329042911768)
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
