package io.kotest.matchers.ints

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

fun Int.shouldBePositive(): Int {
   this shouldBe positive()
   return this
}

fun positive() = object : Matcher<Int> {
   override fun test(value: Int) = MatcherResult(
      value > 0,
      { "$value should be > 0" },
      { "$value should not be > 0" })
}

fun Int.shouldBeNonNegative(): Int {
   this shouldBe nonNegative()
   return this
}

fun nonNegative() = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value >= 0,
         { "$value should be >= 0" },
         { "$value should not be >= 0" })
}

fun Int.shouldBeNegative(): Int {
   this shouldBe negative()
   return this
}

fun negative() = object : Matcher<Int> {
   override fun test(value: Int) = MatcherResult(
      value < 0,
      { "$value should be < 0" },
      { "$value should not be < 0" })
}

fun Int.shouldBeNonPositive(): Int {
   this shouldBe nonPositive()
   return this
}

fun nonPositive() = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value <= 0,
         { "$value should be <= 0" },
         { "$value should not be <= 0" })
}

fun Int.shouldBeEven(): Int {
   this should beEven()
   return this
}

fun Int.shouldNotBeEven(): Int {
   this shouldNot beEven()
   return this
}

fun beEven() = object : Matcher<Int> {
   override fun test(value: Int): MatcherResult =
      MatcherResult(
         value % 2 == 0,
         { "$value should be even" },
         { "$value should be odd" })
}

fun Int.shouldBeOdd(): Int {
   this should beOdd()
   return this
}

fun Int.shouldNotBeOdd(): Int {
   this shouldNot beOdd()
   return this
}

fun beOdd() = object : Matcher<Int> {
   override fun test(value: Int): MatcherResult =
      MatcherResult(
         value % 2 == 1,
         { "$value should be odd" },
         { "$value should be even" })
}

infix fun Int.shouldBeLessThan(x: Int): Int {
   this shouldBe lt(x)
   return this
}

infix fun Int.shouldNotBeLessThan(x: Int): Int {
   this shouldNotBe lt(x)
   return this
}

infix fun Int.shouldBeLessThanOrEqual(x: Int): Int {
   this shouldBe lte(x)
   return this
}

infix fun Int.shouldBeAtMost(x: Int): Int = this.shouldBeLessThanOrEqual(x)
infix fun Int.shouldNotBeAtMost(x: Int): Int = this.shouldBeGreaterThan(x)
infix fun Int.shouldBeAtLeast(x: Int): Int = this.shouldBeGreaterThanOrEqual(x)
infix fun Int.shouldNotBeAtLeast(x: Int): Int = this.shouldBeLessThan(x)

infix fun Int.shouldNotBeLessThanOrEqual(x: Int): Int {
   this shouldNotBe lte(x)
   return this
}

infix fun Int.shouldBeGreaterThan(x: Int): Int {
   this shouldBe gt(x)
   return this
}

infix fun Int.shouldNotBeGreaterThan(x: Int): Int {
   this shouldNotBe gt(x)
   return this
}

infix fun Int.shouldBeGreaterThanOrEqual(x: Int): Int {
   this shouldBe gte(x)
   return this
}

infix fun Int.shouldNotBeGreaterThanOrEqual(x: Int): Int {
   this shouldNotBe gte(x)
   return this
}

infix fun Int.shouldBeExactly(x: Int): Int {
   this shouldBe exactly(x)
   return this
}

infix fun Int.shouldNotBeExactly(x: Int): Int {
   this shouldNotBe exactly(x)
   return this
}

fun Int.shouldBeZero(): Int {
   this shouldBeExactly 0
   return this
}

fun Int.shouldNotBeZero(): Int {
   this shouldNotBeExactly 0
   return this
}
