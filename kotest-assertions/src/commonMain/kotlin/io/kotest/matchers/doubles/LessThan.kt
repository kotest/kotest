package io.kotest.matchers.doubles

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.shouldBe
import io.kotest.shouldNotBe

/**
 * Asserts that this [Double] is strictly less than [x]
 *
 * Verifies that this [Double] is strictly less than [x] (excludes [x] itself).
 *
 * Opposite of [Double.shouldNotBeLessThan]
 *
 * ```
 * 0.1 shouldBeLessThan 0.0   // Assertion fails
 * 0.1 shouldBeLessThan 0.1   // Assertion fails
 * 0.1 shouldBeLessThan 0.2   // Assertion passes
 * ```
 * @see [Double.shouldNotBeGreaterThan]
 * @see [Double.shouldBeLessThanOrEqual]
 */
infix fun Double.shouldBeLessThan(x: Double) = this shouldBe lt(x)

/**
 * Asserts that this [Double] is not strictly less than [x]
 *
 * Opposite of [Double.shouldBeLessThan]
 *
 * ```
 * 0.1 shouldNotBeLessThan 0.0   // Assertion passes
 * 0.1 shouldNotBeLessThan 0.1   // Assertion passes
 * 0.1 shouldNotBeLessThan 0.2   // Assertion fails
 * ```
 *
 * @see [Double.shouldBeGreaterThan]
 * @see [Double.shouldNotBeLessThanOrEqual]
 */
infix fun Double.shouldNotBeLessThan(x: Double) = this shouldNotBe lt(x)

fun lt(x: Double) = beLessThan(x)
fun beLessThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value < x, "$value should be < $x", "$value should not be < $x")
}
