package io.kotlintest.matchers

interface LongMatchers {

  infix fun BeWrapper<Long>.gt(expected: Long): Unit {
    if (value <= expected)
      throw AssertionError("$value is not greater than $expected")
  }

  infix fun BeWrapper<Long>.lt(expected: Long): Unit {
    if (value >= expected)
      throw AssertionError("$value is not less than $expected")
  }

  infix fun BeWrapper<Long>.gte(expected: Long): Unit {
    if (value < expected)
      throw AssertionError("$value is not greater than or equal to $expected")
  }

  infix fun BeWrapper<Long>.lte(expected: Long): Unit {
    if (value > expected)
      throw AssertionError("$value is not less than or equal to $expected")
  }

  fun between(a: Long, b: Long): Matcher<Long> = object : Matcher<Long> {
    override fun test(value: Long) {
      if (a > value || b < value)
        throw AssertionError("$value is not between ($a, $b)")
    }
  }
}