package io.kotest.matchers.ints

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

fun beBetween(a: Int, b: Int) = between(a, b)
fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
  override fun test(value: Int) = MatcherResult(value in a..b,
    "$value is between ($a, $b)",
    "$value is not between ($a, $b)")
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
