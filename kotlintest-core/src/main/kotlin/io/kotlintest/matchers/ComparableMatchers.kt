package io.kotlintest.matchers

fun <T> lt(x: T) = beLessThan(x)
fun <T> beLessThan(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value < x, "$value should be < $x")
}

fun <T> lte(x: T) = beLessThanOrEqualTo(x)
fun <T> beLessThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value <= x, "$value should be <= $x")
}

fun <T> gt(x: T) = beGreaterThan(x)
fun <T> beGreaterThan(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value > x, "$value should be > $x")
}

fun <T> gte(x: T) = beGreaterThanOrEqualTo(x)
fun <T> beGreaterThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = Result(value >= x, "$value should be >= $x")
}
