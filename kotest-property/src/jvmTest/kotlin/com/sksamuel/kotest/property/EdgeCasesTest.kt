package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeCases
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.merge
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single

class EdgeCasesTest : FunSpec() {
   init {
      context("EdgeCases.map") {
         test("should transform static edgecases") {
            edgecases(1, 2, 3).map { it * 5 } shouldBe edgecases(5, 10, 15)
         }

         test("should transform random edgecases") {
            val edges = edgecases { rs -> Arb.int(1..10).single(rs) }
               .merge(edgecases { rs -> Arb.int(11..20).single(rs) })
               .map { "number: $it" }
            edges.values(RandomSource.seeded(1234L)) shouldBe listOf("number: 10", "number: 16")
         }
      }

      context("EdgeCases.flatMap") {
         test("should transform static to static edgecases") {
            val edges = edgecases(1, 2, 3).flatMap { edgecases(it * 2, it * 3, it * 4) }
            edges shouldBe edgecases(
               2, 3, 4,
               4, 6, 8,
               6, 9, 12
            )
         }

         test("should transform static to random edgecases") {
            val edges = edgecases(1, 2, 3).flatMap { value ->
               edgecases { rs -> value to Arb.int(1..10).single(rs) }
            }

            edges.values(RandomSource.seeded(1234L)) shouldContainExactly
               listOf(
                  1 to 10,
                  2 to 6,
                  3 to 2
               )
         }

         test("should transform random to another random edgecases") {
            val edges: EdgeCases<Pair<Int, Int>> =
               edgecases { rs -> Arb.int(1..10).single(rs) }
                  .merge(edgecases { rs -> Arb.int(11..21).single(rs) })
                  .flatMap { value ->
                     edgecases(value to 1, value to 2, value to 3)
                  }
            edges.values(RandomSource.seeded(21234L)) shouldContainExactly
               listOf(
                  7 to 1,
                  7 to 2,
                  7 to 3,
                  11 to 1,
                  11 to 2,
                  11 to 3
               )
         }
      }

      context("EdgeCases.bind") {
         test("should compute cartesian product of edges") {
            data class Container(val a: Int, val b: String)

            val edges = EdgeCases.bind(
               edgecases(1, 2, 3),
               edgecases { rs -> Arb.of("this", "that").single(rs) } +
                  edgecases { rs -> Arb.of("first", "second").single(rs) }
            ) { a, b -> Container(a, b) }
            edges.values(RandomSource.seeded(1234L)) shouldContainExactly listOf(
               Container(a = 1, b = "this"),
               Container(a = 1, b = "second"),
               Container(a = 2, b = "that"),
               Container(a = 2, b = "second"),
               Container(a = 3, b = "that"),
               Container(a = 3, b = "first")
            )
         }
      }

      context("EdgeCases.merge / plus") {
         test("should merge two edgecases") {
            val edgeA = edgecases(1, 2, 3)
            val edgeB = edgecases { rs -> Arb.int(1..10).single(rs) }
            val merged = edgeA.merge(edgeB)
            merged.values(RandomSource.seeded(1234L)) shouldContainExactly listOf(1, 2, 3, 10)
         }

         test("should merge two edgecases (alternate plus operation syntax)") {
            val edgeA = edgecases(1, 2, 3)
            val edgeB = edgecases { rs -> Arb.int(1..10).single(rs) }
            val merged = edgeA + edgeB
            merged.values(RandomSource.seeded(1234L)) shouldContainExactly listOf(1, 2, 3, 10)
         }
      }
   }
}
