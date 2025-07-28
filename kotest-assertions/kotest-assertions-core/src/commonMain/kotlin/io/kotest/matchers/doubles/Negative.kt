package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

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
fun Double.shouldBeNegative(): Double {
   this shouldBe negative()
   return this
}

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
fun Double.shouldNotBeNegative(): Double {
   this shouldNotBe negative()
   return this
}

fun negative() = object : Matcher<Double> {
  override fun test(value: Double) = MatcherResult.invoke(
     value < 0.0,
     { "$value should be < 0.0" },
     { "$value should not be < 0.0" })
}
