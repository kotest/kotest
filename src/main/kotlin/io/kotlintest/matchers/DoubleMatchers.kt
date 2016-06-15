package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface DoubleMatchers {
  infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(this, tolerance)
}

class ToleranceMatcher(val expected: Double, val tolerance: Double) : Matcher<Double> {

  override fun test(map: Double) {
    if (tolerance == 0.0)
      println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe b plusOrMinus c")
    val diff = Math.abs(map - expected)
    if (diff > tolerance)
      throw TestFailedException("$map is not equal to $expected")
  }

  infix fun plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(expected, tolerance)
}