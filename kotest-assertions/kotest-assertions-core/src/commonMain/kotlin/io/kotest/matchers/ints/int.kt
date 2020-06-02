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

fun Int.shouldBePositive() = this shouldBe positive()
fun positive() = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value > 0, "$value should be > 0", "$value should not be > 0")
}

fun Int.shouldBeNegative() = this shouldBe negative()
fun negative() = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value < 0, "$value should be < 0", "$value should not be < 0")
}

fun Int.shouldBeEven() = this should beEven()
fun Int.shouldNotBeEven() = this shouldNot beEven()
fun beEven() = object : Matcher<Int> {
  override fun test(value: Int): MatcherResult =
      MatcherResult(value % 2 == 0, "$value should be even", "$value should be odd")
}

fun Int.shouldBeOdd() = this should beOdd()
fun Int.shouldNotBeOdd() = this shouldNot beOdd()
fun beOdd() = object : Matcher<Int> {
  override fun test(value: Int): MatcherResult =
      MatcherResult(value % 2 == 1, "$value should be odd", "$value should be even")
}

infix fun Int.shouldBeLessThan(x: Int) = this shouldBe lt(x)
infix fun Int.shouldNotBeLessThan(x: Int) = this shouldNotBe lt(x)

infix fun Int.shouldBeLessThanOrEqual(x: Int) = this shouldBe lte(x)
infix fun Int.shouldNotBeLessThanOrEqual(x: Int) = this shouldNotBe lte(x)

infix fun Int.shouldBeGreaterThan(x: Int) = this shouldBe gt(x)
infix fun Int.shouldNotBeGreaterThan(x: Int) = this shouldNotBe gt(x)

infix fun Int.shouldBeGreaterThanOrEqual(x: Int) = this shouldBe gte(x)
infix fun Int.shouldNotBeGreaterThanOrEqual(x: Int) = this shouldNotBe gte(x)

infix fun Int.shouldBeExactly(x: Int) = this shouldBe exactly(x)
infix fun Int.shouldNotBeExactly(x: Int) = this shouldNotBe exactly(x)

fun Int.shouldBeZero() = this shouldBeExactly 0
fun Int.shouldNotBeZero() = this shouldNotBeExactly 0
