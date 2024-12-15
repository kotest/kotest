package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.math.abs
import kotlin.math.absoluteValue

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
   require(tolerance >= 0 && tolerance.isFinite())
   return ToleranceMatcher(this, tolerance)
}

val Number.percent get() = toDouble().percent
val Double.percent: Percentage
get() {
   require(this >= 0 && this.isFinite())
   return Percentage(this)
}
data class Percentage(val value: Double)

/**
 * Creates a matcher for the interval [[this] - [tolerance] , [this] + [tolerance]]
 *
 *
 * ```
 * 1.5 shouldBe (1.0 plusOrMinus 50.percent )   // Assertion passes
 * 1.5 shouldBe (1.0 plusOrMinus 10.percent)   // Assertion fails
 * ```
 */
infix fun Double.plusOrMinus(tolerance: Percentage): ToleranceMatcher {
   val realValue = (this * tolerance.value / 100).absoluteValue
   return ToleranceMatcher(this, realValue)
}

class ToleranceMatcher(private val expected: Double?, private val tolerance: Double) : Matcher<Double?> {

  override fun test(value: Double?): MatcherResult {
    return if (value == null || expected == null || expected.isInfinite()) {
       MatcherResult(
          value == expected,
          { "$value should be equal to $expected" },
          { "$value should not be equal to $expected" }
       )
    } else if (expected.isNaN() && value.isNaN()) {
       println("[WARN] By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776")
       MatcherResult(
          false,
          { "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776" },
          { "By design, Double.Nan != Double.Nan; see https://stackoverflow.com/questions/8819738/why-does-double-nan-double-nan-return-false/8819776#8819776" }
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
       MatcherResult(
          passed,
          { msg },
          { "$value should not be equal to $expected" })
    }
  }
}

/**
 * Verifies that this double is within [percentage]% of [other]
 *
 * 90.0.shouldBeWithinPercentageOf(100.0, 10.0)  // Passes
 * 50.0.shouldBeWithinPercentageOf(100.0, 50.0)  // Passes
 * 30.0.shouldBeWithinPercentageOf(100.0, 10.0)  // Fail
 *
 */
fun Double.shouldBeWithinPercentageOf(other: Double, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this should beWithinPercentageOf(other, percentage)
}

/**
 * Verifies that this double is NOT within [percentage]% of [other]
 *
 * 90.0.shouldNotBeWithinPercentageOf(100.0, 10.0)  // Fail
 * 50.0.shouldNotBeWithinPercentageOf(100.0, 50.0)  // Fail
 * 30.0.shouldNotBeWithinPercentageOf(100.0, 10.0)  // Passes
 *
 */
fun Double.shouldNotBeWithinPercentageOf(other: Double, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this shouldNot beWithinPercentageOf(other, percentage)
}

fun beWithinPercentageOf(other: Double, percentage: Double) = object : Matcher<Double> {
   private val tolerance = other.times(percentage / 100).absoluteValue
   private val range = (other - tolerance)..(other + tolerance)

   override fun test(value: Double) = MatcherResult(
      value in range,
      { "$value should be in $range" },
      {
         "$value should not be in $range"
      })
}
