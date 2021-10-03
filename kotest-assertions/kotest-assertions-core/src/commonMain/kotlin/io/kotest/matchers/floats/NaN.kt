package io.kotest.matchers.floats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that this [Float] is [Float.NaN]
 *
 * Verifies that this [Float] is the Not-a-Number constant [Float.NaN]
 *
 * Opposite of [shouldNotBeNaN]
 *
 * ```
 * Float.NaN.shouldBeNaN()   // Assertion passes
 * 1f.shouldBeNaN()          // Assertion fails
 * ```
 * @see [beNaN]
 */
fun Float.shouldBeNaN(): Float {
   this should beNaN()
   return this
}

/**
 * Assert that this [Float] is not [Float.NaN]
 *
 * Verifies that this [Float] is NOT the Not-a-Number constant [Float.NaN]
 *
 * Opposite of [shouldBeNaN]
 *
 * ```
 * 1f.shouldNotBeNaN()         // Assertion passes
 * Float.NaN.shouldNotBeNaN()  // Assertion fails
 * ```
 * @see [beNaN]
 */
fun Float.shouldNotBeNaN(): Float {
   this shouldNot beNaN()
   return this
}

/**
 * Matcher that matches [Float.NaN]
 *
 * Verifies that a specific [Float] is the Not-a-Number constant [Float.NaN]
 *
 * ```
 *  0.5f should beNaN()          // Assertion fails
 *  Float.NaN should beNaN()     // Assertion passes
 * ```
 *
 * @see [Float.shouldBeNaN]
 * @see [Float.shouldNotBeNaN]
 */
fun beNaN() = object : Matcher<Float> {
   override fun test(value: Float) = MatcherResult(
      value.isNaN(),
      { "$value should be NaN" },
      { "$value should not be NaN" })
}
