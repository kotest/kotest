package io.kotest.matchers.ints

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

/**
 * Verifies that the given integer is between a and b inclusive.
 */
fun Int.shouldBeBetween(a: Int, b: Int) = this shouldBe between(a, b)

/**
 * Verifies that the given integer is NOT between a and b inclusive.
 */
fun Int.shouldNotBeBetween(a: Int, b: Int) = this shouldNot between(a, b)

/**
 * Verifies that the given integer is between a and b inclusive.
 */
fun beBetween(a: Int, b: Int) = between(a, b)

/**
 * Verifies that the given integer is between a and b inclusive.
 */
fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(
     value in a..b,
    "$value should be between ($a, $b)",
    "$value should not be between ($a, $b)"
  )
}

fun lt(x: Int) = beLessThan(x)
fun beLessThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Int) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun gt(x: Int) = beGreaterThan(x)
fun beGreaterThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Int) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value >= x, "$value should be >= $x", "$value should not be >= $x")
}

infix fun Int.shouldBeInRange(range: IntRange) = this should beInRange(range)
infix fun Int.shouldNotBeInRange(range: IntRange) = this shouldNot beInRange(range)
fun beInRange(range: IntRange) = object : Matcher<Int> {
  override fun test(value: Int): MatcherResult =
    MatcherResult(
      value in range,
      "$value should be in range $range",
      "$value should not be in range $range"
    )
}

fun exactly(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(
    value == x,
    "$value should be equal to $x",
    "$value should not be equal to $x"
  )
}
