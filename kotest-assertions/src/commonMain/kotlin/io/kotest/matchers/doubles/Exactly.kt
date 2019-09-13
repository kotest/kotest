package io.kotest.matchers.doubles

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.shouldBe
import io.kotest.shouldNotBe

/**
 * Asserts that this [Double] is exactly [other]
 *
 * Verifies that this [Double] has the same value as [other].
 *
 * Opposite of [Double.shouldNotBeExactly]
 *
 * ```
 * 0.1 shouldBeExactly 0.1   // Assertion passes
 * 0.1 shouldBeExactly 0.2   // Assertion fails
 * ```
 */
infix fun Double.shouldBeExactly(other: Double) = this shouldBe exactly(other)

/**
 * Asserts that this [Double] is not exactly [other]
 *
 * Verifies that this [Double] does not have the same value as [other].
 *
 * Opposite of [Double.shouldBeExactly]
 *
 * ```
 * 0.1 shouldNotBeExactly 0.2   // Assertion passes
 * 0.1 shouldNotBeExactly 0.1   // Assertion fails
 * ```
 */
infix fun Double.shouldNotBeExactly(other: Double) = this shouldNotBe exactly(other)
fun exactly(d: Double): Matcher<Double> = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult(value == d, "$value is not equal to expected value $d", "$value should not equal $d")
}
