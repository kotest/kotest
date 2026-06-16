package io.kotest.matchers.floats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that this float is [Float.POSITIVE_INFINITY]
 *
 * Opposite of [shouldNotBePositiveInfinity]
 *
 * ```
 * Float.POSITIVE_INFINITY.shouldBePositiveInfinity()    // Assertion passes
 * 1.0.shouldBePositiveInfinity()                         // Assertion fails
 * ```
 *
 * @see [bePositiveInfinity]
 */
fun Float.shouldBePositiveInfinity(): Float {
   this should bePositiveInfinity()
   return this
}

/**
 * Verifies that this Float is NOT [Float.POSITIVE_INFINITY]
 *
 * Opposite of [shouldBePositiveInfinity]
 *
 * ```
 * Float.POSITIVE_INFINITY.shouldNotBePositiveInfinity()       // Assertion fails
 * 1.0.shouldBeNotPositiveInfinity()                            // Assertion passes
 * ```
 *
 * @see [bePositiveInfinity]
 */
fun Float.shouldNotBePositiveInfinity(): Float {
   this shouldNot bePositiveInfinity()
   return this
}

/**
 * Matcher that matches whether a Float is [Float.POSITIVE_INFINITY] or not
 *
 * ```
 * Float.POSITIVE_INFINITY should bePositiveInfinity()   // Assertion passes
 * 1.0 should bePositiveInfinity()                        // Assertion fails
 * ```
 */
fun bePositiveInfinity() = object : Matcher<Float> {
   override fun test(value: Float) = MatcherResult(
      value == Float.POSITIVE_INFINITY,
      { "$value should be POSITIVE_INFINITY" },
      { "$value should not be POSITIVE_INFINITY" }
   )
}


/**
 * Verifies that this float is [Float.NEGATIVE_INFINITY]
 *
 * Opposite of [shouldNotBeNegativeInfinity]
 *
 * ```
 * Float.NEGATIVE_INFINITY.shouldBeNegativeInfinity()    // Assertion passes
 * 1.0.shouldBeNegativeInfinity()                         // Assertion fails
 * ```
 *
 * @see [beNegativeInfinity]
 */
fun Float.shouldBeNegativeInfinity(): Float {
   this should beNegativeInfinity()
   return this
}

/**
 * Verifies that this float is NOT [Float.NEGATIVE_INFINITY]
 *
 * Opposite of [shouldBeNegativeInfinity]
 *
 * ```
 * Float.NEGATIVE_INFINITY.shouldNotBeNegativeInfinity()       // Assertion fails
 * 1.0.shouldBeNotNegativeInfinity()                            // Assertion passes
 * ```
 *
 * @see [beNegativeInfinity]
 */
fun Float.shouldNotBeNegativeInfinity(): Float {
   this shouldNot beNegativeInfinity()
   return this
}

/**
 * Matcher that matches whether a float is [Float.NEGATIVE_INFINITY] or not
 *
 * ```
 * Float.NEGATIVE_INFINITY should beNegativeInfinity()   // Assertion passes
 * 1.0 should beNegativeInfinity()                        // Assertion fails
 * ```
 */
fun beNegativeInfinity() = object : Matcher<Float> {
   override fun test(value: Float) = MatcherResult(
      value == Float.NEGATIVE_INFINITY,
      { "$value should be NEGATIVE_INFINITY" },
      { "$value should not be NEGATIVE_INFINITY" }
   )
}
