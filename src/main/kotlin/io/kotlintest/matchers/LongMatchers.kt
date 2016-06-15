package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface LongMatchers {

  infix fun BeWrapper<Long>.gt(expected: Long): Unit {
    if (value <= expected)
      throw TestFailedException("$value is not greater than $expected")
  }

  infix fun BeWrapper<Long>.lt(expected: Long): Unit {
    if (value >= expected)
      throw TestFailedException("$value is not less than $expected")
  }

  infix fun BeWrapper<Long>.gte(expected: Long): Unit {
    if (value < expected)
      throw TestFailedException("$value is not greater than or equal to $expected")
  }

  infix fun BeWrapper<Long>.lte(expected: Long): Unit {
    if (value > expected)
      throw TestFailedException("$value is not less than or equal to $expected")
  }
}