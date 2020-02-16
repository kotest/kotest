package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Double] is strictly greater than [x]
 *
 * Verifies that this [Double] is strictly greater than than [x] (excludes [x] itself]
 *
 * Opposite of [Double.shouldNotBeGreaterThan]
 *
 * ```
 * 0.2 shouldBeGreaterThan 0.1   // Assertion passes
 * 0.2 shouldBeGreaterThan 0.2   // Assertion fails
 * 0.2 shouldBeGreaterThan 0.3   // Assertion fails
 * ```
 *
 * @see [Double.shouldBeGreaterThanOrEqual]
 * @see [Double.shouldNotBeLessThan]
 */
infix fun Double.shouldBeGreaterThan(x: Double) = this shouldBe gt(x)

/**
 * Asserts that this [Double] is not strictly greater than [x]
 *
 * Opposite of [Double.shouldBeGreaterThan]
 *
 * ```
 * 0.2 shouldNotBeGreaterThan 0.1   // Assertion fails
 * 0.2 shouldNotBeGreaterThan 0.2   // Assertion passes
 * 0.2 shouldNotBeGreaterThan 0.3   // Assertion passes
 * ```
 * @see [Double.shouldBeLessThan]
 * @see [Double.shouldNotBeGreaterThanOrEqual]
 */
infix fun Double.shouldNotBeGreaterThan(x: Double) = this shouldNotBe gt(x)

fun gt(x: Double) = beGreaterThan(x)
fun beGreaterThan(x: Double) = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value > x, "$value should be > $x", "$value should not be > $x")
}
