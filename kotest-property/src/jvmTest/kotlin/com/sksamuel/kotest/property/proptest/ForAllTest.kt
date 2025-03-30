package com.sksamuel.kotest.property.proptest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
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


      context("Generated candidate should be reported") {
         test("forAll 1-arity") {
            shouldReportExpectedMessage(arity = 1) {
               forAll(PropTestConfig(1234L), Arb.int()) {
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 2-arity") {
            shouldReportExpectedMessage(arity = 2) {
               forAll(PropTestConfig(1234L), Arb.int(), Arb.int()) { _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 3-arity") {
            shouldReportExpectedMessage(arity = 3) {
               forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int()) { _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 4-arity") {
            shouldReportExpectedMessage(arity = 4) {
               forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 5-arity") {
            shouldReportExpectedMessage(arity = 5) {
               forAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 6-arity") {
            shouldReportExpectedMessage(arity = 6) {
               forAll(
                  PropTestConfig(1234L),
                  Arb.int(),
                  Arb.int(),
                  Arb.int(),
                  Arb.int(),
                  Arb.int(),
                  Arb.int()
               ) { _, _, _, _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 7-arity") {
            shouldReportExpectedMessage(arity = 7) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 8-arity") {
            shouldReportExpectedMessage(arity = 8) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 9-arity") {
            shouldReportExpectedMessage(arity = 9) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 10-arity") {
            shouldReportExpectedMessage(arity = 10) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 11-arity") {
            shouldReportExpectedMessage(arity = 11) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 12-arity") {
            shouldReportExpectedMessage(arity = 12) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 13-arity") {
            shouldReportExpectedMessage(arity = 13) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 14-arity") {
            shouldReportExpectedMessage(arity = 14) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 15-arity") {
            shouldReportExpectedMessage(arity = 15) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 16-arity") {
            shouldReportExpectedMessage(arity = 16) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 17-arity") {
            shouldReportExpectedMessage(arity = 17) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 18-arity") {
            shouldReportExpectedMessage(arity = 18) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 19-arity") {
            shouldReportExpectedMessage(arity = 19) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 20-arity") {
            shouldReportExpectedMessage(arity = 20) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 21-arity") {
            shouldReportExpectedMessage(arity = 21) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("forAll 22-arity") {
            shouldReportExpectedMessage(arity = 22) {
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
                  testContextualArbitrariesGeneration()
               }
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

   private fun PropertyContext.testContextualArbitrariesGeneration(): Boolean {
      val intValue: Int = Arb.int().bind()
      val longValue: Long = Arb.long().bind()

      return intValue.toLong() == longValue
   }

   private suspend fun shouldReportExpectedMessage(arity: Int, checkAllArityFn: suspend () -> Unit) {
      val output = captureStandardOut {
         shouldThrowAny {
            checkAllArityFn()
         }
      }

      val expectedMessage = buildString {
         append("Property test failed for inputs\n\n")
         (0 until arity).forEach { index ->
            append("""$index\) (-)?([0-9]*)\n""")
         }
         append("""$arity\) -744801992 \(generated within property context\)\n""")
         append("""${arity + 1}\) 982709046145343182L \(generated within property context\)\n""")
      }


      output shouldContain expectedMessage.toRegex()
   }
}
