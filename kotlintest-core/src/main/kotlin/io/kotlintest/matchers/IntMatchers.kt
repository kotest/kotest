package io.kotlintest.matchers


fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
  override fun test(value: Int) = Result(a <= value && value <= b, "$value is between ($a, $b)")
}

fun lt(x: Int) = beLessThan(x)
fun beLessThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value < x, "$value should be < $x")
}

fun lte(x: Int) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value <= x, "$value should be <= $x")
}

fun gt(x: Int) = beGreaterThan(x)
fun beGreaterThan(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value > x, "$value should be > $x")
}

fun gte(x: Int) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Int) = object : Matcher<Int> {
  override fun test(value: Int) = Result(value >= x, "$value should be >= $x")
}