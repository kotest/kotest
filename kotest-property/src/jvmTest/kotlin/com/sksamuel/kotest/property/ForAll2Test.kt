package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.exhaustive.constant
import io.kotest.property.exhaustive.ints
import io.kotest.property.forAll

@EnabledIf(LinuxOnlyGithubCondition::class)
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

   test("forAll with 2 implicit arbitraries") {
      val context = forAll<Int, Long> { a, b -> a + b == b + a }
      context.attempts() shouldBe 1000
      context.successes() shouldBe 1000
      context.failures() shouldBe 0
   }

   test("should not throw error if iterations is less than edge cases") {
      shouldNotThrowAny {
         forAll(
            2,
            Arb.int(1..10).withEdgecases(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
         ) { a -> a == a }
      }
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
            PropTestConfig(maxFailure = 5, seed = 1900646515),
            Exhaustive.ints(0..10),
            Exhaustive.ints(20..30)
         ) { a, b -> a > b }
      }.message shouldBe """Property failed after 6 attempts

Repeat this test by using seed 1900646515

Caused by: Property failed 6 times (maxFailure rate was 5)
Last error was caused by args:
  0) 0
  1) 25"""
   }

   test("forAll with minSuccess") {
      shouldThrowAny {
         forAll(
            PropTestConfig(maxFailure = 9, minSuccess = 9, seed = 1921315),
            Exhaustive.ints(0..10),
            Exhaustive.constant(8)
         ) { a, b -> a < b }
      }.message shouldBe """Property failed after 11 attempts

Repeat this test by using seed 1921315

Caused by: Property passed 8 times (minSuccess rate was 9)"""
   }

   test("forAll with a test function that throws should print inputs and perform shrinks") {
      shouldThrowAny {
         forAll(PropTestConfig(1234L), Arb.int(1..10), Arb.string(1..3, Codepoint.alphanumeric())) { number, _ ->
            require(number > 4) { "something unexpected happened" }
            true
         }
      }.message shouldBe """Property failed after 1 attempts

	Arg 0: 1 (shrunk from 2)
	Arg 1: "r" (shrunk from "rDi")

Repeat this test by using seed 1234

Caused by IllegalArgumentException: something unexpected happened"""
   }

   test("forAll with 2 arbs should skip first 4 tests") {
      shouldThrow<AssertionError> {
         forAll(
            config = PropTestConfig(skipTo = 5, seed = 5847062201763421121),
            Exhaustive.ints(0..10),
            Exhaustive.ints(0..10)
         ) { a, b -> a <= b }
      }.message shouldBe """Property failed after 8 attempts

Repeat this test by using seed 5847062201763421121

Caused by AssertionFailedError: expected:<true> but was:<false>"""
   }
})
