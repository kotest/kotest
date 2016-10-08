package io.kotlintest.matchers

@Deprecated("Use `value shouldBe gt(x)` or `value should beGreaterThan(x)`")
infix fun MatcherBuilder<be, Int>.gt(expected: Int): Unit {
  if (value <= expected)
    throw AssertionError("$value should be greater than $expected")
}

@Deprecated("Use `value shouldBe lt(x)` or `value should beLessThan(x)`")
infix fun MatcherBuilder<be, Int>.lt(expected: Int): Unit {
  if (value >= expected)
    throw AssertionError("$value should be less than $expected")
}

@Deprecated("Use `value shouldBe gte(x)` or `value should beGreaterThanOrEqualTo(x)`")
infix fun MatcherBuilder<be, Int>.gte(expected: Int): Unit {
  if (value < expected)
    throw AssertionError("$value should be greater than or equal to $expected")
}

@Deprecated("Use `value shouldBe lte(x)` or `value should beLessOrEqualTo(x)`")
infix fun MatcherBuilder<be, Int>.lte(expected: Int): Unit {
  if (value > expected)
    throw AssertionError("$value should be less than or equal to $expected")
}

interface IntMatchers {

  fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
    override fun test(value: Int) = Result(a <= value && value <= b, "$value is between ($a, $b)")
  }

  fun lt(x:Int) = beLessThan(x)
  fun beLessThan(x: Int) = object : Matcher<Int> {
    override fun test(value: Int) = Result(value < x, "$value should be < $x")
  }

  fun lte(x:Int) = beLessThanOrEqualTo(x)
  fun beLessThanOrEqualTo(x: Int) = object : Matcher<Int> {
    override fun test(value: Int) = Result(value <= x, "$value should be <= $x")
  }

  fun gt(x:Int) = beGreaterThan(x)
  fun beGreaterThan(x: Int) = object : Matcher<Int> {
    override fun test(value: Int) = Result(value > x, "$value should be > $x")
  }

  fun gte(x:Int) = beGreaterThanOrEqualTo(x)
  fun beGreaterThanOrEqualTo(x: Int) = object : Matcher<Int> {
    override fun test(value: Int) = Result(value >= x, "$value should be >= $x")
  }

}