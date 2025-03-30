package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.exhaustive.of

@EnabledIf(LinuxOnlyGithubCondition::class)
class CheckAllExhaustivesIterationTest : FunSpec() {
   init {

      test("checkAll with 1 exhaustive should run once for each value") {
         val context = checkAll(Exhaustive.of(1, 2, 3, 4)) { }
         context.attempts() shouldBe 4
         context.successes() shouldBe 4
         context.failures() shouldBe 0
      }

      test("checkAll with 2 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..100),
            Exhaustive.longs(200L..300L)
         ) { _, _ -> }

         context.attempts() shouldBe 101 * 101
         context.successes() shouldBe 101 * 101
         context.failures() shouldBe 0
      }

      test("checkAll with 3 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..50),
            Exhaustive.ints(0..50),
            Exhaustive.ints(0..50)
         ) { _, _, _ -> }

         context.attempts() shouldBe 51 * 51 * 51
         context.successes() shouldBe 51 * 51 * 51
         context.failures() shouldBe 0
      }

      test("checkAll with 4 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5)
         ) { _, _, _, _ -> }

         context.attempts() shouldBe 6 * 6 * 6 * 6
         context.successes() shouldBe 6 * 6 * 6 * 6
         context.failures() shouldBe 0
      }

      test("checkAll with 5 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5),
            Exhaustive.ints(0..5)
         ) { _, _, _, _, _ -> }

         context.attempts() shouldBe 6 * 6 * 6 * 6 * 6
         context.successes() shouldBe 6 * 6 * 6 * 6 * 6
         context.failures() shouldBe 0
      }

      fun Int.pow(exp: Int) = toBigInteger().pow(exp).toInt()

      test("checkAll with 6 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(6)
         context.successes() shouldBe 2.pow(6)
         context.failures() shouldBe 0

      }

      test("checkAll with 7 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(7)
         context.successes() shouldBe 2.pow(7)
         context.failures() shouldBe 0

      }

      test("checkAll with 8 exhaustives should run for each cross product") {

         val context = checkAll(
            1000,
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
            Exhaustive.ints(0..1),
         ) { _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(8)
         context.successes() shouldBe 2.pow(8)
         context.failures() shouldBe 0

      }

      test("checkAll with 9 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(9)
         context.successes() shouldBe 2.pow(9)
         context.failures() shouldBe 0

      }

      test("checkAll with 10 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(10)
         context.successes() shouldBe 2.pow(10)
         context.failures() shouldBe 0

      }

      test("checkAll with 11 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(11)
         context.successes() shouldBe 2.pow(11)
         context.failures() shouldBe 0

      }

      test("checkAll with 12 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(12)
         context.successes() shouldBe 2.pow(12)
         context.failures() shouldBe 0

      }

      test("checkAll with 13 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(13)
         context.successes() shouldBe 2.pow(13)
         context.failures() shouldBe 0

      }

      test("checkAll with 14 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(14)
         context.successes() shouldBe 2.pow(14)
         context.failures() shouldBe 0

      }

      test("checkAll with 15 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(15)
         context.successes() shouldBe 2.pow(15)
         context.failures() shouldBe 0

      }

      test("checkAll with 16 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(16)
         context.successes() shouldBe 2.pow(16)
         context.failures() shouldBe 0

      }

      test("checkAll with 17 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(17)
         context.successes() shouldBe 2.pow(17)
         context.failures() shouldBe 0

      }

      test("checkAll with 18 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(18)
         context.successes() shouldBe 2.pow(18)
         context.failures() shouldBe 0

      }

      xtest("checkAll with 19 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(19)
         context.successes() shouldBe 2.pow(19)
         context.failures() shouldBe 0

      }

      xtest("checkAll with 20 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(20)
         context.successes() shouldBe 2.pow(20)
         context.failures() shouldBe 0

      }

      xtest("checkAll with 21 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(21)
         context.successes() shouldBe 2.pow(21)
         context.failures() shouldBe 0

      }

      xtest("checkAll with 22 exhaustives should run for each cross product") {

         val context = checkAll(
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
         ) { _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _ -> }

         context.attempts() shouldBe 2.pow(22)
         context.successes() shouldBe 2.pow(22)
         context.failures() shouldBe 0

      }
   }
}
