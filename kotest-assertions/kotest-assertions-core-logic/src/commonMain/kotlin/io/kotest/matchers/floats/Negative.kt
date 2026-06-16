package io.kotest.matchers.floats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Float] is negative
 *
 * Verifies that this [Float] is strictly less than 0.0
 *
 * Opposite of [Float.shouldNotBeNegative]
 *
 * ```
 * 0.1F.shouldBeNegative()      // Assertion fails
 * ```
 *
 * @see [Float.shouldNotBePositive]
 */
fun Float.shouldBeNegative(): Float {
   this shouldBe negative()
   return this
}

/**
 * Asserts that this [Float] is not negative
 *
 * Verifies that this [Float] is not strictly less than 0.0
 *
 * Opposite of [Float.shouldBeNegative]
 *
 * ```
 * 0.1F.shouldNotBeNegative()      // Assertion passes
 * (-0.1F).shouldNotBeNegative()   // Assertion fails
 * ```
 *
 * @see [Float.shouldBePositive]
 */
fun Float.shouldNotBeNegative(): Float {
   this shouldNotBe negative()
   return this
}

fun negative() = object : Matcher<Float> {
   override fun test(value: Float) = MatcherResult.invoke(
      value < 0.0F,
      { "$value should be < 0.0F" },
      { "$value should not be < 0.0F" }
   )
}
