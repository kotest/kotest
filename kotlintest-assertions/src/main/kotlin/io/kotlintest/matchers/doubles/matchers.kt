package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result

fun lt(x: Double) = beLessThan(x)
fun beLessThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Double) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun gt(x: Double) = beGreaterThan(x)
fun beGreaterThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Double) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}