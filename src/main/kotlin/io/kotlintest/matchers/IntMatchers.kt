package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface IntMatchers {

  infix fun BeWrapper<Int>.gt(expected: Int): Unit {
    if (value <= expected)
      throw TestFailedException("$value is not greater than $expected")
  }

  infix fun BeWrapper<Int>.lt(expected: Int): Unit {
    if (value >= expected)
      throw TestFailedException("$value is not less than $expected")
  }

  infix fun BeWrapper<Int>.gte(expected: Int): Unit {
    if (value < expected)
      throw TestFailedException("$value is not greater than or equal to $expected")
  }

  infix fun BeWrapper<Int>.lte(expected: Int): Unit {
    if (value > expected)
      throw TestFailedException("$value is not less than or equal to $expected")
  }
}