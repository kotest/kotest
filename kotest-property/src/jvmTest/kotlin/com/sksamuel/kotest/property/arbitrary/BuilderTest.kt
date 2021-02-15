package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLengthBetween
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
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

      context("edges and edgecases") {
         test("should assign static edges") {
            val arb = arbitrary(listOf(1, 2, 3)) { it.random.nextInt() }
            arb.edgecases(RandomSource.seeded(1234L)) shouldBe listOf(1, 2, 3)
         }

         test("should assign no edges when no edgecases were specified") {
            val arb = arbitrary { it.random.nextInt() }
            arb.edgecases(RandomSource.seeded(1234L)).shouldBeEmpty()
         }
      }
   }
}
