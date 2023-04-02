package com.sksamuel.kotest.property.proptest

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.forAll

class ForAllTest : FunSpec() {
   init {
      context("RandomSource generation within forAll should be consistently generated") {
         test("forAll 1-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(PropTestConfig(1234L), Arb.int()) {
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("forAll 2-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(PropTestConfig(1234L), Arb.int(), Arb.int()) { _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("forAll 3-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int()) { _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("forAll 4-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("forAll 5-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _, _ ->
               testRandomSourcePropagation(expectedRandomSource)
            }
         }

         test("forAll 6-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 7-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 8-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 9-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 10-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 11-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 12-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 13-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 14-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 15-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 16-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 17-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 18-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 19-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 20-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 21-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

         test("forAll 22-arity") {
            val expectedRandomSource = RandomSource.seeded(1234L)
            forAll(
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

   private fun PropertyContext.testRandomSourcePropagation(expectedRandomSource: RandomSource): Boolean {
      val expectedSeed = expectedRandomSource.random.nextLong()
      val seed = randomSource().seed

      val rs = RandomSource.seeded(expectedSeed)
      val intValue: Int = Arb.int().bind()
      val longValue: Long = Arb.long().bind()

      return seed == expectedSeed &&
         intValue == Arb.int().generate(rs, config.edgeConfig).first().value &&
         longValue == Arb.long().generate(rs, config.edgeConfig).first().value
   }
}
