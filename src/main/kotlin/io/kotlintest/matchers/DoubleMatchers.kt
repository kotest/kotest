package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface DoubleMatchers {
  infix fun equal(value: Double): DoubleValueMatcher = DoubleValueMatcher(value)
}

class DoubleValueMatcher(val expected: Double, val tolerance: Double = 0.0) : Matcher<Double> {

  override fun test(value: Double) {
    val diff = Math.abs(value - expected)
    if (diff > tolerance)
      throw TestFailedException("$value is not equal to $expected")
  }

  infix fun plusOrMinus(tolerance: Double): DoubleValueMatcher = DoubleValueMatcher(expected, tolerance)
}