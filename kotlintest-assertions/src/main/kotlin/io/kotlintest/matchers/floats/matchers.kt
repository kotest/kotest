package io.kotlintest.matchers.floats

import io.kotlintest.Matcher
import io.kotlintest.Result

fun lt(x: Float) = beLessThan(x)
fun beLessThan(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun lte(x: Float) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun gt(x: Float) = beGreaterThan(x)
fun beGreaterThan(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun gte(x: Float) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Float) = object : Matcher<Float> {
  override fun test(value: Float) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}