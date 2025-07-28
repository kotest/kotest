package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Double] is positive
 *
 * Verifies that this [Double] is strictly greater than 0.0.
 *
 * Opposite of [Double.shouldNotBePositive]
 *
 * ```
 * 0.1.shouldBePositive()      // Assertion passes
 * (-0.1).shouldBePositive()   // Assertion fails
 * ```
 *
 * @see [Double.shouldNotBeNegative]
 */
fun Double.shouldBePositive(): Double {
   this shouldBe positive()
   return this
}

/**
 * Asserts that this [Double] is not positive
 *
 * Verifies that this [Double] is not strictly greater than 0.0.
 *
 * Opposite of [Double.shouldBePositive]
 *
 * ```
 * 0.1.shouldNotBePositive()      // Assertion fails
 * (-0.1).shouldNotBePositive()   // Assertion passes
 * ```
 *
 * @see [Double.shouldBeNegative]
 */
fun Double.shouldNotBePositive(): Double {
   this shouldNotBe positive()
   return this
}

fun positive() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult.invoke(
     value > 0.0,
     { "$value should be > 0.0" },
     { "$value should not be > 0.0" })
}
