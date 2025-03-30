package com.sksamuel.kotest.property.proptest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

@EnabledIf(LinuxOnlyGithubCondition::class)
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

      context("Generated candidate should be reported") {
         test("checkAll 1-arity") {
            shouldReportExpectedMessage(arity = 1) {
               checkAll(PropTestConfig(1234L), Arb.int()) {
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 2-arity") {
            shouldReportExpectedMessage(arity = 2) {
               checkAll(PropTestConfig(1234L), Arb.int(), Arb.int()) { _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 3-arity") {
            shouldReportExpectedMessage(arity = 3) {
               checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int()) { _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 4-arity") {
            shouldReportExpectedMessage(arity = 4) {
               checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 5-arity") {
            shouldReportExpectedMessage(arity = 5) {
               checkAll(PropTestConfig(1234L), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { _, _, _, _, _ ->
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 6-arity") {
            shouldReportExpectedMessage(arity = 6) {
               checkAll(
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

         test("checkAll 7-arity") {
            shouldReportExpectedMessage(arity = 7) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 8-arity") {
            shouldReportExpectedMessage(arity = 8) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 9-arity") {
            shouldReportExpectedMessage(arity = 9) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 10-arity") {
            shouldReportExpectedMessage(arity = 10) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 11-arity") {
            shouldReportExpectedMessage(arity = 11) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 12-arity") {
            shouldReportExpectedMessage(arity = 12) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 13-arity") {
            shouldReportExpectedMessage(arity = 13) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 14-arity") {
            shouldReportExpectedMessage(arity = 14) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 15-arity") {
            shouldReportExpectedMessage(arity = 15) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 16-arity") {
            shouldReportExpectedMessage(arity = 16) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 17-arity") {
            shouldReportExpectedMessage(arity = 17) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 18-arity") {
            shouldReportExpectedMessage(arity = 18) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 19-arity") {
            shouldReportExpectedMessage(arity = 19) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 20-arity") {
            shouldReportExpectedMessage(arity = 20) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 21-arity") {
            shouldReportExpectedMessage(arity = 21) {
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
                  testContextualArbitrariesGeneration()
               }
            }
         }

         test("checkAll 22-arity") {
            shouldReportExpectedMessage(arity = 22) {
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
                  testContextualArbitrariesGeneration()
               }
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

   private fun PropertyContext.testContextualArbitrariesGeneration() {
      val intValue: Int = Arb.int().bind()
      val longValue: Long = Arb.long().bind()

      intValue shouldBe longValue
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
