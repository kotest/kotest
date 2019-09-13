package io.kotest.matchers.doubles

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.shouldBe
import io.kotest.shouldNotBe

/**
 * Asserts that this [Double] is greater than or equal to [x]
 *
 * Verifies that this [Double] is greater or equal to [x]. This assertion includes [x] itself.
 *
 * Opposite of [Double.shouldNotBeGreaterThanOrEqual]
 *
 * ```
 * 0.2 shouldBeGreaterThanOrEqual 0.1   // Assertion passes
 * 0.2 shouldBeGreaterThanOrEqual 0.2   // Assertion passes
 * 0.2 shouldBeGreaterThanOrEqual 0.3   // Assertion fails
 * ```
 *
 * @see [Double.shouldBeGreaterThan]
 * @see [Double.shouldNotBeLessThanOrEqual]
 */
infix fun Double.shouldBeGreaterThanOrEqual(x: Double) = this shouldBe gte(x)

/**
 * Asserts that this [Double] is not greater than [x] nor equal to [x]
 *
 * Opposite of [Double.shouldBeGreaterThanOrEqual]
 *
 * ```
 * 0.2 shouldNotBeGreaterThanOrEqual 0.1   // Assertion fails
 * 0.2 shouldNotBeGreaterThanOrEqual 0.2   // Assertion fails
 * 0.2 shouldNotBeGreaterThanOrEqual 0.3   // Assertion passes
 * ```
 *
 * @see [Double.shouldNotBeGreaterThan]
 * @see [Double.shouldBeLessThanOrEqual]
 */
infix fun Double.shouldNotBeGreaterThanOrEqual(x: Double) = this shouldNotBe gte(x)

fun gte(x: Double) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value >= x, "$value should be >= $x", "$value should not be >= $x")
}
