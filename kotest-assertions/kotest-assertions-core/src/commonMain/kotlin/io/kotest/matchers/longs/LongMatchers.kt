package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun lt(x: Long) = beLessThan(x)
fun beLessThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Long) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun gt(x: Long) = beGreaterThan(x)
fun beGreaterThan(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Long) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(value >= x, "$value should be >= $x", "$value should not be >= $x")
}

infix fun Long.shouldBeInRange(range: LongRange) = this should beInRange(range)
infix fun Long.shouldNotBeInRange(range: LongRange) = this shouldNot beInRange(range)
fun beInRange(range: LongRange) = object : Matcher<Long> {
  override fun test(value: Long): MatcherResult =
    MatcherResult(
      value in range,
      "$value should be in range $range",
      "$value should not be in range $range"
    )
}

fun exactly(x: Long) = object : Matcher<Long> {
  override fun test(value: Long) = MatcherResult(
    value == x,
    "$value should be equal to $x",
    "$value should not be equal to $x"
  )
}
