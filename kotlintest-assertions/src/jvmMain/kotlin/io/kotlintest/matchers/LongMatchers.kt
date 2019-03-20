package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

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