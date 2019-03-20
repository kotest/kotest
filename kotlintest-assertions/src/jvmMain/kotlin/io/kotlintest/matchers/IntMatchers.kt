package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

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