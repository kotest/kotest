package com.sksamuel.kotest.property.proptest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class CheckAllTest : FunSpec() {
   init {
      context("RandomSource generation within checkAll should be consistently generated") {
         test("checkAll 1-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(PropTestConfig(1234L), Arb.int()) {
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 2-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(PropTestConfig(1234L), Arb.int(), Arb.int()) { _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 3-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int()) { _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 4-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 5-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 6-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 7-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 8-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 9-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 10-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 11-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 12-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 13-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 14-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 15-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 16-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 17-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 18-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 19-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 20-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 21-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("checkAll 22-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            checkAll(
               PropTestConfig(1234L),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int(),
               Arb.int()
            ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }
      }
   }

   private fun PropertyContext.testRandomSourcePropagation(expectedRandomSource: RandomSource) {
      val expectedSeed = expectedRandomSource.random.nextLong()
      randomSource().seed shouldBe expectedSeed
      val rs = RandomSource.seeded(expectedSeed)
      Arb.int().bind() shouldBe Arb.int().generate(rs, config.edgeConfig).first().value
      Arb.long().bind() shouldBe Arb.long().generate(rs, config.edgeConfig).first().value
   }
}
