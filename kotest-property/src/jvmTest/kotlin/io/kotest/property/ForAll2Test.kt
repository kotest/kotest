package io.kotest.property

import io.kotest.shouldBe
import io.kotest.shouldThrowAny
import io.kotest.specs.FunSpec

class ForAll2Test : FunSpec({

   test("forAll with 2 arbitraries") {

      val context = forAll(
         Arbitrary.int(1000),
         Arbitrary.int(10)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 13039
      context.successes() shouldBe 13039
      context.failures() shouldBe 0
   }

   test("forAll with 2 progressions") {

      val context = forAll(
         Progression.int(0..100),
         Progression.long(200L..300L)
      ) { a, b -> a + b == b + a }

      context.attempts() shouldBe 10201
      context.successes() shouldBe 10201
      context.failures() shouldBe 0
   }

   test("forAll with 2 implicit arbitraries") {
      val context = forAll<Int, Long> { a, b -> a + b == b + a }
      context.attempts() shouldBe 10609
      context.successes() shouldBe 10609
      context.failures() shouldBe 0
   }

   test("forAll with mixed arbitrary and progression") {

      val context = forAll(
         Arbitrary.int(1000),
         Progression.int(0..100)
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
            Progression.int(0..10),
            Progression.int(20..30),
            PropTestArgs(maxFailure = 5)
         ) { a, b -> a > b }
      }.message shouldBe """Property failed for
Arg 0: 0
Arg 1: 25
after 6 attempts
Caused by: Prop test failed 6 times (max failure rate was 5)"""
   }

   test("forAll with minSuccess") {
      shouldThrowAny {
         forAll(
            Progression.int(0..10),
            Arbitrary.constant(8),
            PropTestArgs(maxFailure = 9, minSuccess = 9)
         ) { a, b -> a < b }
      }.message shouldBe """Prop test passed 8 times (min success rate was 9)"""
   }
})
