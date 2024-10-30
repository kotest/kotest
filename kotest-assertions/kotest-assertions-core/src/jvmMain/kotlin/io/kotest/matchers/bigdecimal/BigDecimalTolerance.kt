package io.kotest.matchers.bigdecimal

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.doubles.Percentage
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.math.BigDecimal


/**
 * Creates a matcher for the interval [[this] - [tolerance] , [this] + [tolerance]]
 *
 *
 * ```
 * BigDecimal("0.1") shouldBe (BigDecimal("0.4") plusOrMinus BigDecimal("0.5"))   // Assertion passes
 * BigDecimal("0.1") shouldBe (BigDecimal("0.4") plusOrMinus BigDecimal("0.2"))   // Assertion fails
 * ```
 */
infix fun BigDecimal.plusOrMinus(tolerance: BigDecimal): ToleranceMatcher {
   require(tolerance >= BigDecimal.ZERO) { "tolerance must be non-negative, was: $tolerance" }
   return ToleranceMatcher(this, tolerance)
}


/**
 * Creates a matcher for the interval [[this] - [tolerance] , [this] + [tolerance]]
 *
 *
 * ```
 * BigDecimal("1.5") shouldBe (BigDecimal("1.0") plusOrMinus 50.percent )   // Assertion passes
 * BigDecimal("1.5:) shouldBe (BigDecimal("1.0") plusOrMinus 10.percent)   // Assertion fails
 * ```
 */
infix fun BigDecimal.plusOrMinus(tolerance: Percentage): ToleranceMatcher {
   val realValue = (this * BigDecimal(tolerance.value) / BigDecimal(100)).abs()
   return ToleranceMatcher(this, realValue)
}

class ToleranceMatcher(private val expected: BigDecimal?, private val tolerance: BigDecimal) : Matcher<BigDecimal?> {

  override fun test(value: BigDecimal?): MatcherResult {
    return if (value == null || expected == null ) {
       MatcherResult(
          value == expected,
          { "$value should be equal to $expected" },
          {
             "$value should not be equal to $expected"
          })
    } else {
       if (tolerance == BigDecimal.ZERO)
          println("[WARN] When comparing doubles consider using tolerance, eg: a shouldBe (b plusOrMinus c)")
       val diff = (value - expected).abs()

       val passed = diff <= tolerance
       val low = expected - tolerance
       val high = expected + tolerance
       val msg = when (tolerance) {
          BigDecimal.ZERO -> "$value should be equal to $expected"
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
 * BigDecimal("90.0").shouldBeWithinPercentageOf(BigDecimal("100.0"), 10.0)  // Passes
 * BigDecimal("50.0").shouldBeWithinPercentageOf(BigDecimal("100.0"), 50.0)  // Passes
 * BigDecimal("30.0").shouldBeWithinPercentageOf(BigDecimal("100.0:), 10.0)  // Fail
 *
 */
fun BigDecimal.shouldBeWithinPercentageOf(other: BigDecimal, percentage: BigDecimal) {
   require(percentage > BigDecimal.ZERO) { "Percentage must be > 0.0" }
   this should beWithinPercentageOf(other, percentage)
}

/**
 * Verifies that this double is NOT within [percentage]% of [other]
 *
 * BigDecimal("90.0").shouldNotBeWithinPercentageOf(BigDecimal("100.0"), 10.0)  // Fail
 * BigDecimal("50.0").shouldNotBeWithinPercentageOf(BigDecimal("100.0,") 50.0)  // Fail
 * BigDecimal("30.0").shouldNotBeWithinPercentageOf(BigDecimal("100.0"), 10.0)  // Passes
 *
 */
fun BigDecimal.shouldNotBeWithinPercentageOf(other: BigDecimal, percentage: BigDecimal) {
   require(percentage > BigDecimal.ZERO) { "Percentage must be > 0, was: $percentage" }
   this shouldNot beWithinPercentageOf(other, percentage)
}

fun beWithinPercentageOf(other: BigDecimal, percentage: BigDecimal) = object : Matcher<BigDecimal> {
   private val tolerance = other.times(percentage / BigDecimal(100)).abs()
   private val range = (other - tolerance)..(other + tolerance)

   override fun test(value: BigDecimal) = MatcherResult(
      value in range,
      { "$value should be in $range" },
      {
         "$value should not be in $range"
      })
}
