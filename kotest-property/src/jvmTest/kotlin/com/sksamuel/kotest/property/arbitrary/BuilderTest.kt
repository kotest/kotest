package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.arbitraryBuilder
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases
import kotlin.random.nextInt

class BuilderTest : FunSpec() {
   init {

      test("custom arb test") {
         arbitrary {
            it.random.nextInt(3..6)
         }.take(1000).toSet() shouldBe setOf(3, 4, 5, 6)
      }

      test("composition of arbs") {
         data class Person(val name: String, val age: Int)

         val personArb = arbitrary { rs ->
            val name = Arb.string(10..12).next(rs)
            val age = Arb.int(21, 150).next(rs)
            Person(name, age)
         }

         personArb.next().name.shouldHaveLengthBetween(10, 12)
         personArb.next().age.shouldBeBetween(21, 150)
      }

      context("arbitrary builder using continuation") {
         test("should be equivalent to chaining flatMaps") {
            val arbFlatMaps: Arb<String> =
               Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").flatMap { first ->
                  Arb.int(1..9).withEdgecases(5).flatMap { second ->
                     Arb.int(101..109).withEdgecases(100 + second).map { third ->
                        "$first $second $third"
                     }
                  }
               }

            val arb: Arb<String> = arbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..9).withEdgecases(5).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second).bind()
               "$first $second $third"
            }

            val flatMapsResult = arbFlatMaps.generate(RandomSource.seeded(12345L)).take(100).map { it.value }.toList()
            val builderResult = arb.generate(RandomSource.seeded(12345L)).take(100).map { it.value }.toList()

            // should be equivalent
            builderResult shouldContainExactly flatMapsResult
         }

         test("should bind edgecases") {
            val arb: Arb<String> = arbitrary {
               val first = Arb.string(5, Codepoint.alphanumeric()).withEdgecases("edge1", "edge2").bind()
               val second = Arb.int(1..9).withEdgecases(5).bind()
               val third = Arb.int(101..109).withEdgecases(100 + second, 109).bind()
               "$first $second $third"
            }

            arb.edgecases() shouldContainExactlyInAnyOrder setOf(
               "edge1 5 105",
               "edge2 5 105",
               "edge1 5 109",
               "edge2 5 109",
            )
         }

         test("should modify edgecases") {
            val edges = setOf("edge1", "edge2")
            val arb = arbitraryBuilder(edgecaseFn = { edges.random(it.random) }) { "abcd" }

            arb.edgecases() shouldContainExactlyInAnyOrder edges
         }
      }
   }
}
