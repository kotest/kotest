package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface IntMatchers {

  infix fun Be<Int>.gt(expected: Int): Unit {
    if (value <= expected)
      throw TestFailedException("$value is not greater than $expected")
  }

  infix fun Be<Int>.lt(expected: Int): Unit {
    if (value >= expected)
      throw TestFailedException("$value is not less than $expected")
  }

  infix fun Be<Int>.gte(expected: Int): Unit {
    if (value < expected)
      throw TestFailedException("$value is not greater than or equal to $expected")
  }

  infix fun Be<Int>.lte(expected: Int): Unit {
    if (value > expected)
      throw TestFailedException("$value is not less than or equal to $expected")
  }
}