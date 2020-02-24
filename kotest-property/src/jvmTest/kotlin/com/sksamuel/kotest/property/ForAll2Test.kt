package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.PropTestConfig
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.ints
import io.kotest.property.exhaustive.longs
import io.kotest.property.exhaustive.constant
import io.kotest.property.forAll

class ForAll2Test : FunSpec({

   test("forAll with 2 arbs") {

      val context = forAll(
         1000,
         Arb.int(),
         Arb.int()
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("forAll with 2 exhaustives") {

      val context = forAll(
         1000,
         Exhaustive.ints(0..100),
         Exhaustive.longs(200L..300L)
      ) { a, b -> a + b == b + a }

      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("forAll with 2 implicit arbitraries") {
      val context = forAll<Int, Long> { a, b -> a + b == b + a }
      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("should throw error if iteratons is less than min") {
      shouldThrowAny {
         forAll(
            10,
            Exhaustive.ints(0..100),
            Exhaustive.longs(200L..300L)
         ) { a, b -> a + b == b + a }
      }.message shouldBe "Require at least 101 iterations to cover requirements"
   }

   test("forAll with mixed arbitrary and exhaustive") {

      val context = forAll(
         Arb.int(),
         Exhaustive.ints(0..100)
      ) { a, b ->
         a + b == b + a
      }

      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("forAll with shrink mode") {
      forAll(
         PropTestConfig(shrinkingMode = ShrinkingMode.Off),
         Arb.int(),
         Arb.int().map { it * 4 }
      ) { a, b ->
         a + b == b + a
      }
   }

   test("forAll with maxFailure") {
      shouldThrowAny {
         forAll(
            PropTestConfig(maxFailure = 5),
            Exhaustive.ints(0..10),
            Exhaustive.ints(20..30)
         ) { a, b -> a > b }
      }.message shouldStartWith """Property failed after 6 attempts

	Arg 0: 5
	Arg 1: 25

Caused by: Property failed 6 times (maxFailure rate was 5)

Repeat this test by using seed"""
   }

   test("forAll with minSuccess") {
      shouldThrowAny {
         forAll(
            PropTestConfig(maxFailure = 9, minSuccess = 9),
            Exhaustive.ints(0..10),
            Exhaustive.constant(8)
         ) { a, b -> a < b }
      }.message shouldStartWith """Property failed after 42 attempts

	Arg 0: 8
	Arg 1: 8

Caused by: Property failed 10 times (maxFailure rate was 9)

Repeat this test by using seed"""
   }
})
