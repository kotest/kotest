package io.kotlintest.matchers.numerics

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.matchers.between
import io.kotlintest.matchers.gt
import io.kotlintest.matchers.gte
import io.kotlintest.matchers.lt
import io.kotlintest.matchers.lte
import io.kotlintest.matchers.exactly
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe

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

fun Int.shouldBeBetween(a: Int, b: Int) = this shouldBe between(a, b)
fun Int.shouldNotBeBetween(a: Int, b: Int) = this shouldNot between(a, b)

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
