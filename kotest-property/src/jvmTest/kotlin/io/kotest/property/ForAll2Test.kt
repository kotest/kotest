package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.exhaustive.constant
import io.kotest.property.exhaustive.int
import io.kotest.property.exhaustive.long
import io.kotest.property.gen.int
import io.kotest.property.gen.positiveInts
import io.kotest.shouldBe
import io.kotest.shouldThrowAny

class ForAll2Test : FunSpec({

   test("forAll with arbitraries") {

      val context = forAll(
         Gen.int(0..500).take(100),
         Gen.positiveInts().take(10)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 13039
      context.successes() shouldBe 13039
      context.failures() shouldBe 0
   }

   test("forAll with exhaustives") {

      val context = forAll(
         Exhaustive.int(0..100),
         Exhaustive.long(200L..300L)
      ) { a, b -> a + b == b + a }

      context.attempts() shouldBe 10201
      context.successes() shouldBe 10201
      context.failures() shouldBe 0
   }

   test("forAll with implicit arbitraries") {
      val context = forAll<Int, Long> { a, b -> a + b == b + a }
      context.attempts() shouldBe 10609
      context.successes() shouldBe 10609
      context.failures() shouldBe 0
   }

   test("forAll with mixed arbitrary and exhaustive") {

      val context = forAll(
         Gen.int().take(1000),
         Exhaustive.int(0..100)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 101303
      context.successes() shouldBe 101303
      context.failures() shouldBe 0
   }

   test("forAll with maxFailure") {
      shouldThrowAny {
         forAll(
            Exhaustive.int(0..10),
            Exhaustive.int(20..30),
            PropTestConfig(maxFailure = 5)
         ) { a, b -> a > b }
      }.message shouldBe """Property failed for
Arg 0: 0
Arg 1: 25
after 6 attempts
Caused by: Property failed 6 times (maxFailure rate was 5)"""
   }

   test("forAll with minSuccess") {
      shouldThrowAny {
         forAll(
            Exhaustive.int(0..10),
            Exhaustive.constant(8),
            PropTestConfig(maxFailure = 9, minSuccess = 9)
         ) { a, b -> a < b }
      }.message shouldBe """Property failed after 11 attempts
Caused by: Property passed 8 times (minSuccess rate was 9)"""
   }
})
