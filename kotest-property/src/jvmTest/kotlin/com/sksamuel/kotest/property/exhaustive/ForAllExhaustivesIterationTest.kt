package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.exhaustive.of
import io.kotest.property.forAll

@EnabledIf(LinuxCondition::class)
class ForAllExhaustivesIterationTest : FunSpec() {
   init {

      test("forAll with 1 exhaustive should run once for each value") {
         val context = forAll(Exhaustive.of(1, 2, 3, 4)) {
            true
         }
         context.attempts() shouldBe 4
         context.successes() shouldBe 4
         context.failures() shouldBe 0
      }

      test("forAll with 2 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..100),
            Exhaustive.longs(200L..300L)
         ) { _, _ -> true }

         context.attempts() shouldBe 101 * 101
         context.successes() shouldBe 101 * 101
         context.failures() shouldBe 0
      }

      test("forAll with 3 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..50),
            Exhaustive.ints(0..50),
            Exhaustive.ints(0..50)
         ) { _, _, _ -> true }

         context.attempts() shouldBe 51 * 51 * 51
         context.successes() shouldBe 51 * 51 * 51
         context.failures() shouldBe 0
      }

      test("forAll with 4 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5)
         ) { _, _, _, _ -> true }

         context.attempts() shouldBe 6 * 6 * 6 * 6
         context.successes() shouldBe 6 * 6 * 6 * 6
         context.failures() shouldBe 0
      }

      test("forAll with 5 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5)
         ) { _, _, _, _, _ -> true }

         context.attempts() shouldBe 6 * 6 * 6 * 6 * 6
         context.successes() shouldBe 6 * 6 * 6 * 6 * 6
         context.failures() shouldBe 0
      }

      fun Int.pow(exp: Int) = toBigInteger().pow(exp).toInt()

      test("forAll with 6 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(6)
         context.successes() shouldBe 2.pow(6)
         context.failures() shouldBe 0
      }

      test("forAll with 7 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(7)
         context.successes() shouldBe 2.pow(7)
         context.failures() shouldBe 0
      }

      test("forAll with 8 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(8)
         context.successes() shouldBe 2.pow(8)
         context.failures() shouldBe 0
      }

      test("forAll with 9 exhaustives should run for each cross product") {
         val context = forAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(9)
         context.successes() shouldBe 2.pow(9)
         context.failures() shouldBe 0
      }

      test("forAll with 10 exhaustives should run for each cross product") {
         val context = forAll(
            Int.MAX_VALUE,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(10)
         context.successes() shouldBe 2.pow(10)
         context.failures() shouldBe 0
      }

      test("forAll with 11 exhaustives should run for each cross product") {
         val context = forAll(
            Int.MAX_VALUE,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(11)
         context.successes() shouldBe 2.pow(11)
         context.failures() shouldBe 0
      }

      test("forAll with 12 exhaustives should run for each cross product") {
         val context = forAll(
            Int.MAX_VALUE,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(12)
         context.successes() shouldBe 2.pow(12)
         context.failures() shouldBe 0
      }

      test("forAll with 13 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(13)
         context.successes() shouldBe 2.pow(13)
         context.failures() shouldBe 0
      }

      test("forAll with 14 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(14)
         context.successes() shouldBe 2.pow(14)
         context.failures() shouldBe 0
      }

      test("forAll with 15 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(15)
         context.successes() shouldBe 2.pow(15)
         context.failures() shouldBe 0
      }

      test("forAll with 16 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(16)
         context.successes() shouldBe 2.pow(16)
         context.failures() shouldBe 0
      }

      test("forAll with 17 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(17)
         context.successes() shouldBe 2.pow(17)
         context.failures() shouldBe 0
      }

      test("forAll with 18 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(18)
         context.successes() shouldBe 2.pow(18)
         context.failures() shouldBe 0
      }

      test("forAll with 19 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(19)
         context.successes() shouldBe 2.pow(19)
         context.failures() shouldBe 0
      }

      test("forAll with 20 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(20)
         context.successes() shouldBe 2.pow(20)
         context.failures() shouldBe 0
      }

      test("forAll with 21 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(21)
         context.successes() shouldBe 2.pow(21)
         context.failures() shouldBe 0
      }

      test("forAll with 22 exhaustives should run for each cross product") {
         val context = forAll(
            PropTestConfig(iterations = Int.MAX_VALUE),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> true }

         context.attempts() shouldBe 2.pow(22)
         context.successes() shouldBe 2.pow(22)
         context.failures() shouldBe 0
      }
   }
}
