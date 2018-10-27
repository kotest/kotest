package io.kotlintest.matchers.doubles

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlin.math.abs

/**
 * Asserts that this [Double] is between [a] and [b] with a tolerance [tolerance]
 *
 * Verifies that this [Double] has a value higher or equals than [a], or [a] - [tolerance] and lower or equals than [b], or [b] + [tolerance].
 * Opposite of [Double.shouldNotBeBetween]
 *
 * ```
 * 0.5.shouldBeBetween(0.2, 0.7, 0.0)   // Assertion passes
 * 0.5.shouldBeBetween(0.2, 0.3, 0.0)   // Assertion fails
 * 0.5.shouldBeBetween(0.2, 0.3, 0.2)   // Assertion passes
 * 0.5.shouldBeBetween(0.2, 0.3, 0.1)   // Assertion fails
 * ```
 */
fun Double.shouldBeBetween(a: Double, b: Double, tolerance: Double) = this shouldBe between(a, b, tolerance)

/**
 * Asserts that this [Double] is not between [a] and [b] with a tolerance [tolerance]
 *
 * Verifies that this [Double] does not have a value higher or equals than [a], or [a] - [tolerance] and lower or equals than [b], or [b] + [tolerance].
 * Opposite of [Double.shouldBeBetween]
 *
 * ```
 * 0.5.shouldBeBetween(0.2, 0.7, 0.0)   // Assertion fails
 * 0.5.shouldBeBetween(0.2, 0.3, 0.0)   // Assertion passes
 * 0.5.shouldBeBetween(0.2, 0.3, 0.2)   // Assertion fails
 * 0.5.shouldBeBetween(0.2, 0.3, 0.1)   // Assertion passes
 * ```
 */
fun Double.shouldNotBeBetween(a: Double, b: Double, tolerance: Double) = this shouldNotBe between(a, b, tolerance)

fun between(a: Double, b: Double, tolerance: Double): Matcher<Double> = object : Matcher<Double> {
    override fun test(value: Double): Result {
        val differenceToMinimum = value - a
        val differenceToMaximum = b - value

        if (differenceToMinimum < 0 && abs(differenceToMinimum) > tolerance) {
            return Result(false, "$value should be bigger than $a", "$value should not be bigger than $a")
        }

        if (differenceToMaximum < 0 && abs(differenceToMaximum) > tolerance) {
            return Result(false, "$value should be smaller than $b", "$value should not be smaller than $b")
        }

        return Result(true, "$value should be smaller than $b and bigger than $a", "$value should not be smaller than $b and should not be bigger than $a")
    }
}