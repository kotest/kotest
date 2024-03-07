package io.kotest.matchers.longs

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.math.absoluteValue

fun lt(x: Long) = beLessThan(x)
fun beLessThan(x: Long) = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value < x,
         { "$value should be < $x" },
         { "$value should not be < $x" })
}

fun lte(x: Long) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Long) = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value <= x,
         { "$value should be <= $x" },
         { "$value should not be <= $x" })
}

fun gt(x: Long) = beGreaterThan(x)
fun beGreaterThan(x: Long) = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value > x,
         { "$value should be > $x" },
         { "$value should not be > $x" })
}

fun gte(x: Long) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Long) = object : Matcher<Long> {
   override fun test(value: Long) =
      MatcherResult(
         value >= x,
         { "$value should be >= $x" },
         { "$value should not be >= $x" })
}

infix fun Long.shouldBeInRange(range: LongRange) = this should beInRange(range)
infix fun Long.shouldNotBeInRange(range: LongRange) = this shouldNot beInRange(range)
fun beInRange(range: LongRange) = object : Matcher<Long> {
   override fun test(value: Long): MatcherResult =
      MatcherResult(
         value in range,
         { "$value should be in range $range" },
         { "$value should not be in range $range" })
}

fun exactly(x: Long) = object : Matcher<Long> {
   override fun test(value: Long) = MatcherResult(
      value == x,
      { "$value should be equal to $x" },
      { "$value should not be equal to $x" })
}


/**
 * Verifies that this long is within [percentage]% of [other]
 *
 * 90.shouldBeWithinPercentageOf(100, 10.0)  // Passes
 * 50.shouldBeWithinPercentageOf(100, 50.0)  // Passes
 * 30.shouldBeWithinPercentageOf(100, 10.0)  // Fail
 *
 */
fun Long.shouldBeWithinPercentageOf(other: Long, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this should beWithinPercentageOf(other, percentage)
}

/**
 * Verifies that this long is NOT within [percentage]% of [other]
 *
 * 90.shouldNotBeWithinPercentageOf(100, 10.0)  // Fail
 * 50.shouldNotBeWithinPercentageOf(100, 50.0)  // Fail
 * 30.shouldNotBeWithinPercentageOf(100, 10.0)  // Passes
 *
 */
fun Long.shouldNotBeWithinPercentageOf(other: Long, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this shouldNot beWithinPercentageOf(other, percentage)
}

fun beWithinPercentageOf(other: Long, percentage: Double) = object : Matcher<Long> {
   private val tolerance = other.times(percentage / 100).absoluteValue
   private val range = (other - tolerance)..(other + tolerance)

   override fun test(value: Long) = MatcherResult(
      value.toDouble() in range,
      { "$value should be in $range" },
      { "$value should not be in $range" })
}
