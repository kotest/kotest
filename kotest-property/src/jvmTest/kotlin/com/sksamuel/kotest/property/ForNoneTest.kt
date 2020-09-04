package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.exhaustive.constant
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.forNone

class ForNoneTest : FunSpec({
   test("forNone with 2 arbs") {

      val context = forNone(
         1000,
         Arb.int(),
         Arb.int()
      ) { a, b ->
         a + b != b + a
      }

      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("forNone with 2 implicit arbitraries") {
      val context = forNone<Int, Long> { a, b -> a + b != b + a }
      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("should throw error if iteratons is less than min") {
      shouldThrowAny {
         forNone(
            10,
            Exhaustive.ints(0..100),
            Exhaustive.longs(200L..300L)
         ) { a, b -> a + b != b + a }
      }.message shouldBe "Require at least 101 iterations to cover requirements"
   }

   test("forNone with mixed arbitrary and exhaustive") {

      val context = forNone(
         Arb.int(),
         Exhaustive.ints(0..100)
      ) { a, b ->
         a + b != b + a
      }

      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("forNone with shrink mode") {
      forNone(
         PropTestConfig(shrinkingMode = ShrinkingMode.Off),
         Arb.int(),
         Arb.int().map { it * 4 }
      ) { a, b ->
         a + b != b + a
      }
   }

   test("forNone with maxFailure") {
      shouldThrowAny {
         forNone(
            PropTestConfig(maxFailure = 5, seed = 1900646515),
            Exhaustive.ints(0..10),
            Exhaustive.ints(20..30)
         ) { a, b -> a < b }
      }.message shouldBe """Property failed after 6 attempts

Repeat this test by using seed 1900646515

Caused by: Property failed 6 times (maxFailure rate was 5)
Last error was caused by args:
  0) 0
  1) 25"""
   }

   test("forNone with minSuccess") {
      shouldThrowAny {
         forNone(
            PropTestConfig(maxFailure = 9, minSuccess = 9, seed = 1921315),
            Exhaustive.ints(0..10),
            Exhaustive.constant(8)
         ) { a, b -> a < b }
      }.message shouldBe """Property failed after 11 attempts

Repeat this test by using seed 1921315

Caused by: Property passed 3 times (minSuccess rate was 9)"""
   }
})
