package io.kotlintest.matchers

infix fun ShouldBuilder<be, Long>.gt(expected: Long): Unit {
  if (value <= expected)
    throw AssertionError("$value is not greater than $expected")
}

infix fun ShouldBuilder<be, Long>.lt(expected: Long): Unit {
  if (value >= expected)
    throw AssertionError("$value is not less than $expected")
}

infix fun ShouldBuilder<be, Long>.gte(expected: Long): Unit {
  if (value < expected)
    throw AssertionError("$value is not greater than or equal to $expected")
}

infix fun ShouldBuilder<be, Long>.lte(expected: Long): Unit {
  if (value > expected)
    throw AssertionError("$value is not less than or equal to $expected")
}

interface LongMatchers {

  fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
    override fun test(value: Long) {
      if (a > value || b < value)
        throw AssertionError("$value is not between ($a, $b)")
    }
  }
}