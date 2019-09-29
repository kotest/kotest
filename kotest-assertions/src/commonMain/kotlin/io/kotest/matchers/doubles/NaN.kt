package io.kotest.matchers.doubles

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

/**
 * Asserts that this [Double] is [Double.NaN]
 *
 * Verifies that this [Double] is the Not-a-Number constant [Double.NaN]
 *
 * Opposite of [shouldNotBeNaN]
 *
 * ```
 * Double.NaN.shouldBeNaN()   // Assertion passes
 * 1.0.shouldBeNaN()          // Assertion fails
 * ```
 * @see [beNaN]
 */
fun Double.shouldBeNaN() = this should beNaN()

/**
 * Assert that this [Double] is not [Double.NaN]
 *
 * Verifies that this [Double] is NOT the Not-a-Number constant [Double.NaN]
 *
 * Opposite of [shouldBeNaN]
 *
 * ```
 * 1.0.shouldNotBeNaN()         // Assertion passes
 * Double.NaN.shouldNotBeNaN()  // Assertion fails
 * ```
 * @see [beNaN]
 */
fun Double.shouldNotBeNaN() = this shouldNot beNaN()

/**
 * Matcher that matches [Double.NaN]
 *
 * Verifies that a specific [Double] is the Not-a-Number constant [Double.NaN]
 *
 * ```
 *  0.5 should beNaN()            // Assertion fails
 *  Double.NaN should beNaN()     // Assertion passes
 * ```
 *
 * @see [Double.shouldBeNaN]
 * @see [Double.shouldNotBeNaN]
 */
fun beNaN() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(
    value.isNaN(),
    "$value should be NaN",
    "$value should not be NaN"
  )
}
