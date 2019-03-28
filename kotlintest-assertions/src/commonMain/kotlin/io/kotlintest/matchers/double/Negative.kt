package io.kotlintest.matchers.double

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

/**
 * Asserts that this [Double] is negative
 *
 * Verifies that this [Double] is strictly less than 0.0
 *
 * Opposite of [Double.shouldNotBeNegative]
 *
 * ```
 * 0.1.shouldBeNegative()      // Assertion fails
 * (-0.1).shouldBeNegative()   // Assertion passes
 * ```
 *
 * @see [Double.shouldNotBePositive]
 */
fun Double.shouldBeNegative() = this shouldBe negative()

/**
 * Asserts that this [Double] is not negative
 *
 * Verifies that this [Double] is not strictly less than 0.0
 *
 * Opposite of [Double.shouldBeNegative]
 *
 * ```
 * 0.1.shouldNotBeNegative()      // Assertion passes
 * (-0.1).shouldNotBeNegative()   // Assertion fails
 * ```
 *
 * @see [Double.shouldBePositive]
 */
fun Double.shouldNotBeNegative() = this shouldNotBe negative()

fun negative() = object : Matcher<Double> {
  override fun test(value: Double) = Result(value < 0.0,
      "$value should be < 0.0",
      "$value should not be < 0.0")
}