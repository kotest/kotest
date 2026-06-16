package io.kotest.matchers.ints

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.comparables.between
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlin.math.absoluteValue

/**
 * Verifies that the given integer is between [a, b] (inclusive, inclusive).
 */
@Deprecated(
   "Int-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Int import `io.kotest.matchers.ints.shouldBeBetween` manually.",
   ReplaceWith("shouldBeBetween(a, b)", "io.kotest.matchers.comparables.shouldBeBetween")
)
fun Int.shouldBeBetween(a: Int, b: Int) = this shouldBe between(a, b)

/**
 * Verifies that the given integer is NOT between [a, b] (inclusive, inclusive).
 */
@Deprecated(
   "Int-specific assertion is getting replaced with a new Comparable assertion of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Int import `io.kotest.matchers.ints.shouldNotBeBetween` manually.",
   ReplaceWith("shouldNotBeBetween(a, b)", "io.kotest.matchers.comparables.shouldNotBeBetween")
)
fun Int.shouldNotBeBetween(a: Int, b: Int) = this shouldNot between(a, b)

/**
 * Verifies that the given integer is between [a, b] (inclusive, inclusive).
 */
@Deprecated(
   "Int-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Int import `io.kotest.matchers.ints.beBetween` manually.",
   ReplaceWith("beBetween(a, b)", "io.kotest.matchers.comparables.beBetween")
)
fun beBetween(a: Int, b: Int) = between(a, b)

/**
 * Verifies that the given integer is between a and b inclusive.
 */
@Deprecated(
   "Int-specific matcher is getting replaced with a new Comparable matcher of the same name.\nNote: If you perform the offered IDE autocorrection, you still need to remove the Int import `io.kotest.matchers.ints.between` manually.",
   ReplaceWith("between(a, b)", "io.kotest.matchers.comparables.between")
)
fun between(a: Int, b: Int): Matcher<Int> = between(a, b)

fun lt(x: Int) = beLessThan(x)
fun beLessThan(x: Int) = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value < x,
         { "$value should be < $x" },
         { "$value should not be < $x" })
}

fun lte(x: Int) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Int) = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value <= x,
         { "$value should be <= $x" },
         { "$value should not be <= $x" })
}

fun gt(x: Int) = beGreaterThan(x)
fun beGreaterThan(x: Int) = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value > x,
         { "$value should be > $x" },
         { "$value should not be > $x" })
}

fun gte(x: Int) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Int) = object : Matcher<Int> {
   override fun test(value: Int) =
      MatcherResult(
         value >= x,
         { "$value should be >= $x" },
         { "$value should not be >= $x" })
}

/**
 * Match that verifies a given integer is within the given [IntRange].
 */
infix fun Int.shouldBeInRange(range: IntRange): Int {
   this should beInRange(range)
   return this
}

/**
 * Match that verifies a given integer is not within the given [IntRange].
 */
infix fun Int.shouldNotBeInRange(range: IntRange): Int {
   this shouldNot beInRange(range)
   return this
}

/**
 * Match that verifies a given integer is within the given [IntRange].
 */
fun beInRange(range: IntRange) = object : Matcher<Int> {
   override fun test(value: Int): MatcherResult =
      MatcherResult(
         value in range,
         { "$value should be in range $range" },
         { "$value should not be in range $range" }
      )
}

fun exactly(x: Int) = object : Matcher<Int> {
   override fun test(value: Int) = MatcherResult(
      value == x,
      { "$value should be equal to $x" },
      { "$value should not be equal to $x" }
   )
}

/**
 * Verifies that this int is within [percentage]% of [other]
 *
 * 90.shouldBeWithinPercentageOf(100, 10.0)  // Passes
 * 50.shouldBeWithinPercentageOf(100, 50.0)  // Passes
 * 30.shouldBeWithinPercentageOf(100, 10.0)  // Fail
 *
 */
fun Int.shouldBeWithinPercentageOf(other: Int, percentage: Double): Int {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this should beWithinPercentageOf(other, percentage)
   return this
}

/**
 * Verifies that this int is NOT within [percentage]% of [other]
 *
 * 90.shouldNotBeWithinPercentageOf(100, 10.0)  // Fail
 * 50.shouldNotBeWithinPercentageOf(100, 50.0)  // Fail
 * 30.shouldNotBeWithinPercentageOf(100, 10.0)  // Passes
 *
 */
fun Int.shouldNotBeWithinPercentageOf(other: Int, percentage: Double): Int {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this shouldNot beWithinPercentageOf(other, percentage)
   return this
}

fun beWithinPercentageOf(other: Int, percentage: Double) = object : Matcher<Int> {
   private val tolerance = other.times(percentage / 100).absoluteValue
   private val range = (other - tolerance)..(other + tolerance)

   override fun test(value: Int) = MatcherResult(
      value.toDouble() in range,
      { "$value should be in $range" },
      { "$value should not be in $range" },
   )
}
