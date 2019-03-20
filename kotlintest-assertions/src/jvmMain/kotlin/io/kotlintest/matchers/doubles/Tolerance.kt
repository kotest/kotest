package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result

/**
 * Creates a matcher for the interval [[this] - [tolerance] , [this] + [tolerance]]
 *
 *
 * ```
 * 0.1 shouldBe (0.4 plusOrMinus 0.5)   // Assertion passes
 * 0.1 shouldBe (0.4 plusOrMinus 0.2)   // Assertion fails
 * ```
 */
infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher = ToleranceMatcher(this, tolerance)

class ToleranceMatcher(private val expected: Double?, private val tolerance: Double) : Matcher<Double?> {
  override fun test(value: Double?): Result {
    return if(value == null || expected == null) {
      Result(value == expected,
          "$value should be equal to $expected",
          "$value should not be equal to $expected")
    } else if (Double.NaN == expected && Double.NaN == value) {
      println("[WARN] By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
      Result(false,
          "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776",
          "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776"
      )
    } else {
      if (tolerance == 0.0)
        println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe (b plusOrMinus c)")
      val diff = Math.abs(value - expected)
      Result(diff <= tolerance,
          "$value should be equal to $expected",
          "$value should not be equal to $expected")
    }
  }
}