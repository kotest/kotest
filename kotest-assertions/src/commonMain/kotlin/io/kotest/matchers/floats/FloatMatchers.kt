package io.kotest.matchers.floats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.math.abs

infix fun Float.plusOrMinus(tolerance: Float): FloatToleranceMatcher = FloatToleranceMatcher(this, tolerance)

class FloatToleranceMatcher(private val expected: Float, private val tolerance: Float) : Matcher<Float> {

  override fun test(value: Float): MatcherResult {
    return if (expected.isNaN() && value.isNaN()) {
      println("[WARN] By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
      MatcherResult(
          false,
          "By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776",
          "By design, Float.Nan != Float.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776"
      )
    } else {
      if (tolerance == 0.0F)
        println("[WARN] When comparing Float consider using tolerance, eg: a shouldBe b plusOrMinus c")
      val diff = abs(value - expected)
      MatcherResult(diff <= tolerance, "$value should be equal to $expected", "$value should not be equal to $expected")
    }
  }

  infix fun plusOrMinus(tolerance: Float): FloatToleranceMatcher = FloatToleranceMatcher(expected, tolerance)
}
