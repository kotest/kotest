package com.sksamuel.kotest.properties.shrinking

import io.kotest.matchers.comparables.lte
import io.kotest.matchers.doubles.lt
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.properties.Gen
import io.kotest.properties.PropertyTesting
import io.kotest.properties.assertAll
import io.kotest.properties.double
import io.kotest.properties.int
import io.kotest.properties.negativeIntegers
import io.kotest.properties.positiveIntegers
import io.kotest.properties.shrinking.ChooseShrinker
import io.kotest.properties.shrinking.Shrinker
import io.kotest.properties.shrinking.StringShrinker
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.shouldThrowAny
import io.kotest.specs.StringSpec

class ShrinkTest : StringSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   "should report shrinked values for arity 1 ints" {
      shouldThrowAny {
         assertAll(Gen.int()) {
            it.shouldBeLessThan(5)
         }
      }.message shouldBe "Property failed for\n" +
         "Arg 0: 5 (shrunk from 2147483647)\n" +
         "after 2 attempts\n" +
         "Caused by: 2147483647 should be < 5"
   }

   "should shrink arity 2 strings" {
      shouldThrowAny {
         assertAll(Gen.string(), Gen.string()) { a, b ->
            (a.length + b.length).shouldBeLessThan(4)
         }
      }.message shouldBe "Property failed for\n" +
         "Arg 0: <empty string>\n" +
         "Arg 1: aaaaa (shrunk from \n" +
         "abc\n" +
         "123\n" +
         ")\n" +
         "after 3 attempts\n" +
         "Caused by: 9 should be < 4"
   }

   "should shrink arity 3 positiveIntegers" {
      shouldThrowAny {
         assertAll(Gen.positiveIntegers(), Gen.positiveIntegers(), Gen.positiveIntegers()) { a, b, c ->
            a.toLong() + b.toLong() + c.toLong() shouldBe 4L
         }
      }.message shouldBe "Property failed for\nArg 0: 1 (shrunk from 2147483647)\nArg 1: 1 (shrunk from 2147483647)\nArg 2: 1 (shrunk from 2147483647)\nafter 1 attempts\nCaused by: expected: 4L but was: 6442450941L"
   }

   "should shrink arity 4 negativeIntegers" {
      shouldThrowAny {
         assertAll(Gen.negativeIntegers(),
            Gen.negativeIntegers(),
            Gen.negativeIntegers(),
            Gen.negativeIntegers()) { a, b, c, d ->
            a + b + c + d shouldBe 4
         }
      }.message shouldBe "Property failed for\nArg 0: -1 (shrunk from -2147483648)\nArg 1: -1 (shrunk from -2147483648)\nArg 2: -1 (shrunk from -2147483648)\nArg 3: -1 (shrunk from -2147483648)\nafter 1 attempts\nCaused by: expected: 4 but was: 0"
   }

   "should shrink arity 1 doubles" {
      shouldThrowAny {
         assertAll(Gen.double()) { a ->
            a shouldBe lt(3.0)
         }
      }.message shouldBe "Property failed for\nArg 0: 3.0 (shrunk from 1.0E300)\nafter 4 attempts\nCaused by: 1.0E300 should be < 3.0"
   }

   "should shrink Gen.choose" {
      shouldThrowAny {
         assertAll(object : Gen<Int> {
            override fun constants(): Iterable<Int> = emptyList()
            override fun random(seed: Long?): Sequence<Int> = generateSequence { 14 }
            override fun shrinker() = ChooseShrinker(5, 15)
         }) { a ->
            a shouldBe lte(10)
         }
      }.message shouldBe "Property failed for\nArg 0: 11 (shrunk from 14)\nafter 1 attempts\nCaused by: 14 should be <= 10"
   }

   "should shrink strings to empty string" {
      val gen = object : Gen<String> {
         override fun random(seed: Long?): Sequence<String> = generateSequence { "asjfiojoqiwehuoahsuidhqweqwe" }
         override fun constants(): Iterable<String> = emptyList()
         override fun shrinker(): Shrinker<String>? = StringShrinker
      }
      shouldThrowAny {
         assertAll(gen) { a ->
            a.shouldHaveLength(10)
         }
      }.message shouldBe "Property failed for\nArg 0: <empty string> (shrunk from asjfiojoqiwehuoahsuidhqweqwe)\nafter 1 attempts\nCaused by: asjfiojoqiwehuoahsuidhqweqwe should have length 10, but instead was 28"
   }

   "should shrink strings to min failing size" {
      val gen = object : Gen<String> {
         override fun random(seed: Long?): Sequence<String> = generateSequence { "asjfiojoqiwehuoahsuidhqweqwe" }
         override fun constants(): Iterable<String> = emptyList()
         override fun shrinker(): Shrinker<String>? = StringShrinker
      }
      shouldThrowAny {
         assertAll(gen) { a ->
            a.padEnd(10, '*').shouldHaveLength(10)
         }
      }.message shouldBe "Property failed for\nArg 0: aaaaaaaaaaaaaa (shrunk from asjfiojoqiwehuoahsuidhqweqwe)\nafter 1 attempts\nCaused by: asjfiojoqiwehuoahsuidhqweqwe should have length 10, but instead was 28"
   }
})
