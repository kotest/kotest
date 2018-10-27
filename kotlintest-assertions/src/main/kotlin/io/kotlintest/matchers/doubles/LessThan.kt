package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

/**
 * Asserts that this [Double] is less than [x]
 *
 * Verifies that this [Double] has a lower value than [x].
 * Opposite of [Double.shouldNotBeLessThan]
 *
 * ```
 * 0.1.shouldBeLessThan(0.0)   // Assertion fails
 * 0.1.shouldBeLessThan(0.1)   // Assertion fails
 * 0.1.shouldBeLessThan(0.2)   // Assertion passes
 * ```
 */
infix fun Double.shouldBeLessThan(x: Double) = this shouldBe lt(x)

/**
 * Asserts that this [Double] is not less than [x]
 *
 * Verifies that this [Double] does not have a lower value than [x].
 * Opposite of [Double.shouldBeLessThan]
 *
 * ```
 * 0.1.shouldNotBeLessThan(0.0)   // Assertion passes
 * 0.1.shouldNotBeLessThan(0.1)   // Assertion passes
 * 0.1.shouldNotBeLessThan(0.2)   // Assertion fails
 * ```
 */
infix fun Double.shouldNotBeLessThan(x: Double) = this shouldNotBe lt(x)

fun lt(x: Double) = beLessThan(x)
fun beLessThan(x: Double) = object : Matcher<Double> {
    override fun test(value: Double) = Result(value < x, "$value should be < $x", "$value should not be < $x")
}

/**
 * Asserts that this [Double] is less or equals than [x]
 *
 * Verifies that this [Double] has a lower or equal value than [x].
 * Opposite of [Double.shouldNotBeLessThanOrEqual]
 *
 * ```
 * 0.1.shouldBeLessThanOrEqual(0.0)   // Assertion fails
 * 0.1.shouldBeLessThanOrEqual(0.1)   // Assertion passes
 * 0.1.shouldBeLessThanOrEqual(0.2)   // Assertion passes
 * ```
 */
infix fun Double.shouldBeLessThanOrEqual(x: Double) = this shouldBe lte(x)

/**
 * Asserts that this [Double] is not less or equals than [x]
 *
 * Verifies that this [Double] does not have a lower or equal value than [x].
 * Opposite of [Double.shouldBeLessThanOrEqual]
 *
 * ```
 * 0.1.shouldNotBeLessThanOrEqual(0.0)   // Assertion passes
 * 0.1.shouldNotBeLessThanOrEqual(0.1)   // Assertion fails
 * 0.1.shouldNotBeLessThanOrEqual(0.2)   // Assertion fails
 * ```
 */
infix fun Double.shouldNotBeLessThanOrEqual(x: Double) = this shouldNotBe lte(x)

fun lte(x: Double) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Double) = object : Matcher<Double> {
    override fun test(value: Double) = Result(value <= x, "$value should be <= $x", "$value should not be <= $x")
}