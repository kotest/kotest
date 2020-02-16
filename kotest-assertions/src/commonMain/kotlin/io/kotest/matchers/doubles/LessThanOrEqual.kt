package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Double] is less than or equal to [x]
 *
 * Verifies that this [Double] is less than or equal to [x]. This assertion includes [x] itself
 *
 * Opposite of [Double.shouldNotBeLessThanOrEqual]
 *
 * ```
 * 0.1 shouldBeLessThanOrEqual 0.0   // Assertion fails
 * 0.1 shouldBeLessThanOrEqual 0.1   // Assertion passes
 * 0.1 shouldBeLessThanOrEqual 0.2   // Assertion passes
 * ```
 *
 * @see [Double.shouldBeLessThan]
 * @see [Double.shouldNotBeGreaterThanOrEqual]
 */
infix fun Double.shouldBeLessThanOrEqual(x: Double) = this shouldBe lte(x)

/**
 * Asserts that this [Double] is not less than [x] nor equal to [x]
 *
 * Opposite of [Double.shouldBeLessThanOrEqual]
 *
 * ```
 * 0.1 shouldNotBeLessThanOrEqual 0.0   // Assertion passes
 * 0.1 shouldNotBeLessThanOrEqual 0.1   // Assertion fails
 * 0.1 shouldNotBeLessThanOrEqual 0.2   // Assertion fails
 * ```
 *
 * @see [Double.shouldNotBeLessThan]
 * @see [Double.shouldBeGreaterThanOrEqual]
 */
infix fun Double.shouldNotBeLessThanOrEqual(x: Double) = this shouldNotBe lte(x)

fun lte(x: Double) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value <= x, "$value should be <= $x", "$value should not be <= $x")
}
