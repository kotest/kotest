package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.math.abs

/**
 * Creates a matcher for the interval [[this] - [tolerance] , [this] + [tolerance]]
 *
 *
 * ```
 * 0.1 shouldBe (0.4 plusOrMinus 0.5)   // Assertion passes
 * 0.1 shouldBe (0.4 plusOrMinus 0.2)   // Assertion fails
 * ```
 */
infix fun Double.plusOrMinus(tolerance: Double): ToleranceMatcher {
   require(tolerance >= 0)
   return ToleranceMatcher(this, tolerance)
}

class ToleranceMatcher(private val expected: Double?, private val tolerance: Double) : Matcher<Double?> {

  override fun test(value: Double?): MatcherResult {
    return if(value == null || expected == null) {
      MatcherResult(value == expected, "$value should be equal to $expected", "$value should not be equal to $expected")
    } else if (expected.isNaN() && value.isNaN()) {
       println("[WARN] By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
       MatcherResult(
          false,
          "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776",
          "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776"
       )
    } else {
       if (tolerance == 0.0)
          println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe (b plusOrMinus c)")
       val diff = abs(value - expected)

       val passed = diff <= tolerance
       val low = expected - tolerance
       val high = expected + tolerance
       val msg = when (tolerance) {
          0.0 -> "$value should be equal to $expected"
          else -> "$value should be equal to $expected within tolerance of $tolerance (lowest acceptable value is $low; highest acceptable value is $high)"
       }
       MatcherResult(passed, msg,"$value should not be equal to $expected")
    }
  }
}
