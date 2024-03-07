package io.kotest.matchers.longs

import io.kotest.matchers.comparables.gt
import io.kotest.matchers.comparables.gte
import io.kotest.matchers.comparables.lt
import io.kotest.matchers.comparables.lte
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

fun Long.shouldBePositive(): Long {
   this shouldBe positiveL()
   return this
}

fun positiveL() = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value > 0,
         { "$value should be > 0" },
         { "$value should not be > 0" })
}

fun Long.shouldBeNonNegative(): Long {
   this shouldBe nonNegativeL()
   return this
}

fun nonNegativeL() = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value >= 0,
         { "$value should be >= 0" },
         { "$value should not be >= 0" })
}

fun Long.shouldBeNegative(): Long {
   this shouldBe negativeL()
   return this
}

fun negativeL() = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value < 0,
         { "$value should be < 0" },
         { "$value should not be < 0" })
}

fun Long.shouldBeNonPositive(): Long {
   this shouldBe nonPositiveL()
   return this
}

fun nonPositiveL() = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value <= 0,
         { "$value should be <= 0" },
         { "$value should not be <= 0" })
}

fun Long.shouldBeEven(): Long {
   this should lbeEven()
   return this
}

fun Long.shouldNotBeEven(): Long {
   this shouldNot lbeEven()
   return this
}

fun lbeEven() = object : Matcher<Long> {
   override fun test(value: Long): MatcherResult =
      MatcherResult(
         value % 2 == 0L,
         { "$value should be even" },
         { "$value should be odd" })
}

fun Long.shouldBeOdd(): Long {
   this should lbeOdd()
   return this
}

fun Long.shouldNotBeOdd(): Long {
   this shouldNot lbeOdd()
   return this
}

fun lbeOdd() = object : Matcher<Long> {
   override fun test(value: Long): MatcherResult =
      MatcherResult(
         value % 2 == 1L,
         { "$value should be odd" },
         { "$value should be even" })
}

infix fun Long.shouldBeLessThan(x: Long): Long {
   this shouldBe lt(x)
   return this
}

infix fun Long.shouldNotBeLessThan(x: Long): Long {
   this shouldNotBe lt(x)
   return this
}

infix fun Long.shouldBeLessThanOrEqual(x: Long): Long {
   this shouldBe lte(x)
   return this
}

infix fun Long.shouldNotBeLessThanOrEqual(x: Long): Long {
   this shouldNotBe lte(x)
   return this
}

infix fun Long.shouldBeGreaterThan(x: Long): Long {
   this shouldBe gt(x)
   return this
}

infix fun Long.shouldNotBeGreaterThan(x: Long): Long {
   this shouldNotBe gt(x)
   return this
}

infix fun Long.shouldBeGreaterThanOrEqual(x: Long): Long {
   this shouldBe gte(x)
   return this
}

infix fun Long.shouldNotBeGreaterThanOrEqual(x: Long): Long {
   this shouldNotBe gte(x)
   return this
}

infix fun Long.shouldBeExactly(x: Long): Long {
   this shouldBe exactly(x)
   return this
}

infix fun Long.shouldNotBeExactly(x: Long): Long {
   this shouldNotBe exactly(x)
   return this
}

fun Long.shouldBeZero(): Long {
   this shouldBeExactly 0L
   return this
}

fun Long.shouldNotBeZero(): Long {
   this shouldNotBeExactly 0L
   return this
}
