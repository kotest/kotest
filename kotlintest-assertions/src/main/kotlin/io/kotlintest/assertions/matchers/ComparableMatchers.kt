package io.kotlintest.assertions.matchers

fun <T : Comparable<T>> lt(x: T) = beLessThan(x)
fun <T : Comparable<T>> beLessThan(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

fun <T : Comparable<T>> lte(x: T) = beLessThanOrEqualTo(x)
fun <T : Comparable<T>> beLessThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}

fun <T : Comparable<T>> gt(x: T) = beGreaterThan(x)
fun <T : Comparable<T>> beGreaterThan(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

fun <T : Comparable<T>> gte(x: T) = beGreaterThanOrEqualTo(x)
fun <T : Comparable<T>> beGreaterThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}
