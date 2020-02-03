package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.ints
import io.kotest.property.arbitrary.map
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.exhaustive.single
import io.kotest.property.forAll

class ForAll2Test : FunSpec({

   test("forAll with 2 arbs") {

      val context = forAll(
         Arb.ints().take(1000),
         Arb.ints().take(10)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 13039
      context.successes() shouldBe 13039
      context.failures() shouldBe 0
   }

   test("forAll with 2 exhaustives") {

      val context = forAll(
         Exhaustive.ints(0..100),
         Exhaustive.longs(200L..300L)
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

   test("forAll with mixed arbitrary and exhaustive") {

      val context = forAll(
         Arb.ints().take(1000),
         Exhaustive.ints(0..100)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 101303
      context.successes() shouldBe 101303
      context.failures() shouldBe 0
   }

   test("forAll with shrink mode") {
      forAll(
         Arb.ints().take(1000, ShrinkingMode.Off),
         Arb.ints().map { it * 4 }.take(1000, ShrinkingMode.Off)
      ) { a, b ->
         a + b == b + a
      }
   }

   test("forAll with maxFailure") {
      shouldThrowAny {
         forAll(
            Exhaustive.ints(0..10),
            Exhaustive.ints(20..30),
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
            Exhaustive.ints(0..10),
            Exhaustive.single(8),
            PropTestConfig(maxFailure = 9, minSuccess = 9)
         ) { a, b -> a < b }
      }.message shouldBe """Property failed after 11 attempts
Caused by: Property passed 8 times (minSuccess rate was 9)"""
   }
})
