package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.property.arbitrary.int
import io.kotest.property.progression.constant
import io.kotest.property.progression.int
import io.kotest.property.progression.long
import io.kotest.shouldBe
import io.kotest.shouldThrowAny
import kotlinx.coroutines.delay

class CheckAll2Test : FunSpec({

   test("checkAll with 2 arbitraries") {

      val context = checkAll(
         Arbitrary.int(1000),
         Arbitrary.int(10)
      ) { a, b ->
         delay(10)
         a + b shouldBe b + a
      }

      context.attempts() shouldBe 13039
      context.successes() shouldBe 13039
      context.failures() shouldBe 0
   }

   test("checkAll with 2 progressions") {

      val context = checkAll(
         Progression.int(0..100),
         Progression.long(200L..300L)
      ) { a, b -> a + b shouldBe b + a }

      context.attempts() shouldBe 10201
      context.successes() shouldBe 10201
      context.failures() shouldBe 0
   }

   test("checkAll with 2 implicit arbitraries") {
      val context = checkAll<Int, Long> { a, b -> a + b shouldBe b + a }
      context.attempts() shouldBe 10609
      context.successes() shouldBe 10609
      context.failures() shouldBe 0
   }

   test("checkAll with 2 implicit arbitraries using a suspend function") {

      val context = forAll<Int, Long>(15) { a, b ->
         delay(10)
         a + b == b + a
      }
      context.attempts() shouldBe 324
      context.successes() shouldBe 324
      context.failures() shouldBe 0
   }

   test("checkAll with mixed arbitrary and progression") {

      val context = checkAll(
         Arbitrary.int(1000),
         Progression.int(0..100)
      ) { a, b ->
         a + b shouldBe b + a
      }

      context.attempts() shouldBe 101303
      context.successes() shouldBe 101303
      context.failures() shouldBe 0
   }

   test("checkAll with maxFailure") {
      shouldThrowAny {
         checkAll(
            Progression.int(0..10),
            Progression.int(20..30),
            PropTestArgs(maxFailure = 5)
         ) { a, b -> a shouldBeGreaterThan b }
      }.message shouldBe """Property failed for
Arg 0: 0
Arg 1: 25
after 6 attempts
Caused by: Property failed 6 times (maxFailure rate was 5)"""
   }

   test("checkAll with minSuccess") {
      shouldThrowAny {
         checkAll(
            Progression.int(0..10),
            Progression.constant(8),
            PropTestArgs(maxFailure = 9, minSuccess = 9)
         ) { a, b -> a shouldBeGreaterThan b }
      }.message shouldBe """Property failed after 11 attempts
Caused by: Property passed 2 times (minSuccess rate was 9)"""
   }
})
