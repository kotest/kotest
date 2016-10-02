package io.kotlintest.matchers

infix fun ShouldBuilder<be, Int>.gt(expected: Int): Unit {
  if (value <= expected)
    throw AssertionError("$value is not greater than $expected")
}

infix fun ShouldBuilder<be, Int>.lt(expected: Int): Unit {
  if (value >= expected)
    throw AssertionError("$value is not less than $expected")
}

infix fun ShouldBuilder<be, Int>.gte(expected: Int): Unit {
  if (value < expected)
    throw AssertionError("$value is not greater than or equal to $expected")
}

infix fun ShouldBuilder<be, Int>.lte(expected: Int): Unit {
  if (value > expected)
    throw AssertionError("$value is not less than or equal to $expected")
}

interface IntMatchers {

  fun between(a: Int, b: Int): Matcher<Int> = object : Matcher<Int> {
    override fun test(value: Int) {
      if (a > value || b < value)
        throw AssertionError("$value is not between ($a, $b)")
    }
  }
}