package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

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
fun Double.shouldBePositive() = this shouldBe positive()

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
fun Double.shouldNotBePositive() = this shouldNotBe positive()

fun positive() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value > 0.0, "$value should be > 0.0", "$value should not be > 0.0")
}
