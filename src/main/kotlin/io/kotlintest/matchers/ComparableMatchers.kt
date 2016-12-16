package io.kotlintest.matchers

interface ComparableMatchers {

  infix fun <T> BeWrapper<T>.gt(expected: Comparable<T>): Unit {
    if (expected >= value)
      throw AssertionError("$value is not greater than $expected")
  }

  infix fun <T> BeWrapper<T>.lt(expected: Comparable<T>): Unit {
    if (expected <= value)
      throw AssertionError("$value is not less than $expected")
  }

  infix fun <T> BeWrapper<T>.gte(expected: Comparable<T>): Unit {
    if (expected > value)
      throw AssertionError("$value is not greater than or equal to $expected")
  }

  infix fun <T> BeWrapper<T>.lte(expected: Comparable<T>): Unit {
    if (expected < value)
      throw AssertionError("$value is not less than or equal to $expected")
  }

}