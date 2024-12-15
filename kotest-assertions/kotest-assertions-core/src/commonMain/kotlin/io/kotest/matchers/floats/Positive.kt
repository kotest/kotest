package io.kotest.matchers.floats

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult.Companion.invoke
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Float] is positive
 *
 * Verifies that this [Float] is strictly greater than 0.0.
 *
 * Opposite of [Float.shouldNotBePositive]
 *
 * ```
 * 0.1F.shouldBePositive()      // Assertion passes
 * (-0.1F).shouldBePositive()   // Assertion fails
 * ```
 *
 * @see [Float.shouldNotBeNegative]
 */
fun Float.shouldBePositive(): Float {
   this shouldBe positive()
   return this
}

/**
 * Asserts that this [Float] is not positive
 *
 * Verifies that this [Float] is not strictly greater than 0.0.
 *
 * Opposite of [Float.shouldBePositive]
 *
 * ```
 * 0.1F.shouldNotBePositive()      // Assertion fails
 * (-0.1F).shouldNotBePositive()   // Assertion passes
 * ```
 *
 * @see [Float.shouldBeNegative]
 */
fun Float.shouldNotBePositive(): Float {
   this shouldNotBe positive()
   return this
}

fun positive() = object : Matcher<Float> {
   override fun test(value: Float) = invoke(
      value > 0.0F,
      { "$value should be > 0.0F" },
      { "$value should not be > 0.0F" }
   )
}
