package io.kotlintest.matchers

@Deprecated("Use `value shouldBe lt(x)` or `value should beLessThan(x)`")
infix fun <T> MatcherBuilder<be, T>.lt(expected: Comparable<T>): Unit {
  if (expected <= value)
    throw AssertionError("$value is not less than $expected")
}

@Deprecated("Use `value shouldBe lte(x)` or `value should beLessOrEqualTo(x)`")
infix fun <T> MatcherBuilder<be, T>.lte(expected: Comparable<T>): Unit {
  if (expected < value)
    throw AssertionError("$value should be less than or equal to $expected")
}

@Deprecated("Use `value shouldBe gt(x)` or `value should beGreaterThan(x)`")
infix fun <T> MatcherBuilder<be, T>.gt(expected: Comparable<T>): Unit {
  if (expected >= value)
    throw AssertionError("$value is not less than $expected")
}

@Deprecated("Use `value shouldBe gte(x)` or `value should beGreaterThanOrEqualTo(x)`")
infix fun <T> MatcherBuilder<be, T>.gte(expected: Comparable<T>): Unit {
  if (expected > value)
    throw AssertionError("$value should be less than or equal to $expected")
}

interface ComparableMatchers {

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

}