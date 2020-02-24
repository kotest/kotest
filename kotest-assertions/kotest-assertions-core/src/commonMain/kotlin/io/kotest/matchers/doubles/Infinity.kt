package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

/**
 * Verifies that this double is [Double.POSITIVE_INFINITY]
 *
 * Opposite of [shouldNotBePositiveInfinity]
 *
 * ```
 * Double.POSITIVE_INFINITY.shouldBePositiveInfinity()    // Assertion passes
 * 1.0.shouldBePositiveInfinity()                         // Assertion fails
 * ```
 *
 * @see [bePositiveInfinity]
 */
fun Double.shouldBePositiveInfinity() = this should bePositiveInfinity()

/**
 * Verifies that this double is NOT [Double.POSITIVE_INFINITY]
 *
 * Opposite of [shouldBePositiveInfinity]
 *
 * ```
 * Double.POSITIVE_INFINITY.shouldNotBePositiveInfinity()       // Assertion fails
 * 1.0.shouldBeNotPositiveInfinity()                            // Assertion passes
 * ```
 *
 * @see [bePositiveInfinity]
 */
fun Double.shouldNotBePositiveInfinity() = this shouldNot bePositiveInfinity()

/**
 * Matcher that matches whether a double is [Double.POSITIVE_INFINITY] or not
 *
 * ```
 * Double.POSITIVE_INFINITY should bePositiveInfinity()   // Assertion passes
 * 1.0 should bePositiveInfinity()                        // Assertion fails
 * ```
 */
fun bePositiveInfinity() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(
    value == Double.POSITIVE_INFINITY,
    "$value should be POSITIVE_INFINITY",
    "$value should not be POSITIVE_INFINITY"
  )
}


/**
 * Verifies that this double is [Double.NEGATIVE_INFINITY]
 *
 * Opposite of [shouldNotBeNegativeInfinity]
 *
 * ```
 * Double.NEGATIVE_INFINITY.shouldBeNegativeInfinity()    // Assertion passes
 * 1.0.shouldBeNegativeInfinity()                         // Assertion fails
 * ```
 *
 * @see [beNegativeInfinity]
 */
fun Double.shouldBeNegativeInfinity() = this should beNegativeInfinity()

/**
 * Verifies that this double is NOT [Double.NEGATIVE_INFINITY]
 *
 * Opposite of [shouldBeNegativeInfinity]
 *
 * ```
 * Double.NEGATIVE_INFINITY.shouldNotBeNegativeInfinity()       // Assertion fails
 * 1.0.shouldBeNotNegativeInfinity()                            // Assertion passes
 * ```
 *
 * @see [beNegativeInfinity]
 */
fun Double.shouldNotBeNegativeInfinity() = this shouldNot beNegativeInfinity()

/**
 * Matcher that matches whether a double is [Double.NEGATIVE_INFINITY] or not
 *
 * ```
 * Double.NEGATIVE_INFINITY should beNegativeInfinity()   // Assertion passes
 * 1.0 should beNegativeInfinity()                        // Assertion fails
 * ```
 */
fun beNegativeInfinity() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(
    value == Double.NEGATIVE_INFINITY,
    "$value should be NEGATIVE_INFINITY",
    "$value should not be NEGATIVE_INFINITY"
  )
}
