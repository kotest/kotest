package io.kotlintest.matchers.integers

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe

fun Int.shouldBePositive() = this shouldBe positive()
fun positive() = object : Matcher<Int> {
  override fun test(value: Int) = Result(value > 0, "$value should be > 0", "$value should not be > 0")
}

fun Int.shouldBeNegative() = this shouldBe negative()
fun negative() = object : Matcher<Int> {
  override fun test(value: Int) = Result(value < 0, "$value should be < 0", "$value should not be < 0")
}

fun Int.shouldBeEven() = this should beEven()
fun Int.shouldNotBeEven() = this shouldNot beEven()
fun beEven() = object : Matcher<Int> {
  override fun test(value: Int): Result =
      Result(value % 2 == 0, "$value should be even", "$value should be odd")
}

fun Int.shouldBeOdd() = this should beOdd()
fun Int.shouldNotBeOdd() = this shouldNot beOdd()
fun beOdd() = object : Matcher<Int> {
  override fun test(value: Int): Result =
      Result(value % 2 == 1, "$value should be odd", "$value should be even")
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

infix fun Int.shouldBeInRange(range: IntRange) = this should beInRange(range)
infix fun Int.shouldNotBeInRange(range: IntRange) = this shouldNot beInRange(range)
fun beInRange(range: IntRange) = object : Matcher<Int> {
  override fun test(value: Int): Result =
          Result(
                  value in range,
                  "$value should be in range $range",
                  "$value should not be in range $range"
          )
}


fun beBetween(a: Int, b: Int) = between(a, b)
fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
  override fun test(value: Int) = Result(value in a..b,
          "$value is between ($a, $b)",
          "$value is not between ($a, $b)")
}

fun lt(x: Int) = beLessThan(x)
fun beLessThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Int) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value <= x,
          "$value should be <= $x",
          "$value should not be <= $x")
}

fun gt(x: Int) = beGreaterThan(x)
fun beGreaterThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Int) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value >= x,
          "$value should be >= $x",
          "$value should not be >= $x")
}