package io.kotlintest.matchers

@Deprecated("Use `value shouldBe gt(x)` or `value should beGreaterThan(x)`")
infix fun MatcherBuilder<be, Long>.gt(expected: Long): Unit {
  if (value <= expected)
    throw AssertionError("$value is not greater than $expected")
}

@Deprecated("Use `value shouldBe lt(x)` or `value should beLessThan(x)`")
infix fun MatcherBuilder<be, Long>.lt(expected: Long): Unit {
  if (value >= expected)
    throw AssertionError("$value is not less than $expected")
}

@Deprecated("Use `value shouldBe gte(x)` or `value should beGreaterThanOrEqualTo(x)`")
infix fun MatcherBuilder<be, Long>.gte(expected: Long): Unit {
  if (value < expected)
    throw AssertionError("$value is not greater than or equal to $expected")
}

@Deprecated("Use `value shouldBe lte(x)` or `value should beLessOrEqualTo(x)`")
infix fun MatcherBuilder<be, Long>.lte(expected: Long): Unit {
  if (value > expected)
    throw AssertionError("$value is not less than or equal to $expected")
}

interface LongMatchers {

  fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
    override fun test(value: Long) = Result(a <= value && value <= b, "$value is between ($a, $b)")
  }

  fun lt(x:Long) = beLessThan(x)
  fun beLessThan(x: Long) = object : Matcher<Long> {
    override fun test(value: Long) = Result(value < x, "$value should be < $x")
  }

  fun lte(x:Long) = beLessThanOrEqualTo(x)
  fun beLessThanOrEqualTo(x: Long) = object : Matcher<Long> {
    override fun test(value: Long) = Result(value <= x, "$value should be <= $x")
  }

  fun gt(x:Long) = beGreaterThan(x)
  fun beGreaterThan(x: Long) = object : Matcher<Long> {
    override fun test(value: Long) = Result(value > x, "$value should be > $x")
  }

  fun gte(x:Long) = beGreaterThanOrEqualTo(x)
  fun beGreaterThanOrEqualTo(x: Long) = object : Matcher<Long> {
    override fun test(value: Long) = Result(value >= x, "$value should be >= $x")
  }
}