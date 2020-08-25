package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.exhaustive.of
import io.kotest.property.forAll

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
   }
}
