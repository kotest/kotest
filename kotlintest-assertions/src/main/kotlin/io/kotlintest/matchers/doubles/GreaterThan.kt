package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

/**
 * Asserts that this [Double] is greater than [x]
 *
 * Verifies that this [Double] has a higher value than [x].
 * Opposite of [Double.shouldNotBeGreaterThan]
 *
 * ```
 * 0.2.shouldBeGreaterThan(0.1)   // Assertion passes
 * 0.2.shouldBeGreaterThan(0.2)   // Assertion fails
 * 0.2.shouldBeGreaterThan(0.3)   // Assertion fails
 * ```
 */
infix fun Double.shouldBeGreaterThan(x: Double) = this shouldBe gt(x)

/**
 * Asserts that this [Double] is not greater than [x]
 *
 * Verifies that this [Double] does not have a higher value than [x].
 * Opposite of [Double.shouldBeGreaterThan]
 *
 * ```
 * 0.2.shouldNotBeGreaterThan(0.1)   // Assertion fails
 * 0.2.shouldNotBeGreaterThan(0.2)   // Assertion passes
 * 0.2.shouldNotBeGreaterThan(0.3)   // Assertion passes
 * ```
 */
infix fun Double.shouldNotBeGreaterThan(x: Double) = this shouldNotBe gt(x)

fun gt(x: Double) = beGreaterThan(x)
fun beGreaterThan(x: Double) = object : Matcher<Double> {
    override fun test(value: Double) = Result(value > x, "$value should be > $x", "$value should not be > $x")
}

/**
 * Asserts that this [Double] is greater or equals than [x]
 *
 * Verifies that this [Double] has a higher or equal value than [x].
 * Opposite of [Double.shouldNotBeGreaterThanOrEqual]
 *
 * ```
 * 0.2.shouldBeGreaterThanOrEqual(0.1)   // Assertion passes
 * 0.2.shouldBeGreaterThanOrEqual(0.2)   // Assertion passes
 * 0.2.shouldBeGreaterThanOrEqual(0.3)   // Assertion fails
 * ```
 */
infix fun Double.shouldBeGreaterThanOrEqual(x: Double) = this shouldBe gte(x)

/**
 * Asserts that this [Double] is not greater or equals than [x]
 *
 * Verifies that this [Double] does not have a higher or equals value than [x].
 * Opposite of [Double.shouldBeGreaterThanOrEqual]
 *
 * ```
 * 0.2.shouldNotBeGreaterThanOrEqual(0.1)   // Assertion fails
 * 0.2.shouldNotBeGreaterThanOrEqual(0.2)   // Assertion fails
 * 0.2.shouldNotBeGreaterThanOrEqual(0.3)   // Assertion passes
 * ```
 */
infix fun Double.shouldNotBeGreaterThanOrEqual(x: Double) = this shouldNotBe gte(x)

fun gte(x: Double) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Double) = object : Matcher<Double> {
    override fun test(value: Double) = Result(value >= x, "$value should be >= $x", "$value should not be >= $x")
}