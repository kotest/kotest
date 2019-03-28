package io.kotlintest.matchers.longs

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe

fun Long.shouldBePositive() = this shouldBe positiveL()
fun positiveL() = object : Matcher<Long> {
  override fun test(value: Long) = Result(value > 0, "$value should be > 0", "$value should not be > 0")
}

fun Long.shouldBeNegative() = this shouldBe negativeL()
fun negativeL() = object : Matcher<Long> {
  override fun test(value: Long) = Result(value < 0, "$value should be < 0", "$value should not be < 0")
}

fun Long.shouldBeEven() = this should lbeEven()
fun Long.shouldNotBeEven() = this shouldNot lbeEven()
fun lbeEven() = object : Matcher<Long> {
  override fun test(value: Long): Result =
      Result(value % 2 == 0L, "$value should be even", "$value should be odd")
}

fun Long.shouldBeOdd() = this should lbeOdd()
fun Long.shouldNotBeOdd() = this shouldNot lbeOdd()
fun lbeOdd() = object : Matcher<Long> {
  override fun test(value: Long): Result =
      Result(value % 2 == 1L, "$value should be odd", "$value should be even")
}

infix fun Long.shouldBeLessThan(x: Long) = this shouldBe lt(x)
infix fun Long.shouldNotBeLessThan(x: Long) = this shouldNotBe lt(x)

infix fun Long.shouldBeLessThanOrEqual(x: Long) = this shouldBe lte(x)
infix fun Long.shouldNotBeLessThanOrEqual(x: Long) = this shouldNotBe lte(x)

infix fun Long.shouldBeGreaterThan(x: Long) = this shouldBe gt(x)
infix fun Long.shouldNotBeGreaterThan(x: Long) = this shouldNotBe gt(x)

infix fun Long.shouldBeGreaterThanOrEqual(x: Long) = this shouldBe gte(x)
infix fun Long.shouldNotBeGreaterThanOrEqual(x: Long) = this shouldNotBe gte(x)


infix fun Long.shouldBeInRange(range: LongRange) = this should beInRange(range)
infix fun Long.shouldNotBeInRange(range: LongRange) = this shouldNot beInRange(range)
fun beInRange(range: LongRange) = object : Matcher<Long> {
  override fun test(value: Long): Result =
          Result(
                  value in range,
                  "$value should be in range $range",
                  "$value should not be in range $range"
          )
}

fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
  override fun test(value: Long) = Result(value in a..b,
          "$value is between ($a, $b)",
          "$value is not between ($a, $b)")
}

fun lt(x: Long) = beLessThan(x)
fun beLessThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Long) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value <= x,
          "$value should be <= $x",
          "$value should not be <= $x")
}

fun gt(x: Long) = beGreaterThan(x)
fun beGreaterThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Long) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = Result(value >= x,
          "$value should be >= $x",
          "$value should not be >= $x")
}