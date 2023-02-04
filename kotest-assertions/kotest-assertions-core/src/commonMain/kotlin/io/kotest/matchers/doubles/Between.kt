package io.kotest.matchers.doubles

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResult.Companion.invoke
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Asserts that this [Double] is in the interval [[a]-[tolerance] , [b]+[tolerance]]
 *
 * Verifies that this [Double] is greater than or equal to ([a] - [tolerance]) and less than or equal to ([b] + [tolerance])
 *
 * Opposite of [Double.shouldNotBeBetween]
 *
 * ```
 * 0.5.shouldBeBetween(0.2, 0.7, 0.0)   // Assertion passes
 * 0.5.shouldBeBetween(0.2, 0.3, 0.0)   // Assertion fails
 * 0.5.shouldBeBetween(0.2, 0.3, 0.2)   // Assertion passes
 * 0.5.shouldBeBetween(0.2, 0.3, 0.1)   // Assertion fails
 * ```
 */
fun Double.shouldBeBetween(a: Double, b: Double, tolerance: Double): Double {
   this shouldBe between(a, b, tolerance)
   return this
}

/**
 * Asserts that this [Double] is NOT in the interval [[a]-[tolerance] , [b]+[tolerance]]
 *
 * Verifies that this [Double] is not:
 * - Greater than or equal to ([a] - [tolerance])
 * - Less than or equal to ([b] + [tolerance])
 *
 * If and only if both the assertions are true, which means that this [Double] is in the interval, this assertion fails.
 *
 * Opposite of [Double.shouldBeBetween]
 *
 *
 * ```
 * 0.5.shouldNotBeBetween(0.2, 0.7, 0.0)   // Assertion fails
 * 0.5.shouldNotBeBetween(0.2, 0.3, 0.0)   // Assertion passes
 * 0.5.shouldNotBeBetween(0.2, 0.3, 0.2)   // Assertion fails
 * 0.5.shouldNotBeBetween(0.2, 0.3, 0.1)   // Assertion passes
 * ```
 */
fun Double.shouldNotBeBetween(a: Double, b: Double, tolerance: Double): Double {
   this shouldNotBe between(a, b, tolerance)
   return this
}

/**
 * Matcher that matches doubles and intervals
 *
 * Verifies that a specific [Double] is in the interval [[a] - [tolerance] , [b] + [tolerance]].
 *
 * For example:
 *
 * 0.5 is in the interval [0.4 , 0.6], because 0.4 <= 0.5 <= 0.6.
 *
 * This matcher also includes the bonds of the interval, so:
 *
 * 0.5 is in the interval [0.5, 0.6] because 0.5 <= 0.5 <= 0.6.
 *
 * The parameter [tolerance] is used to allow a slightly wider range, to include possible imprecision, and can be 0.0.
 *
 * 0.5 is in the interval [0.6, 0.7] when there's a tolerance of 0.1, because (0.6 - 0.1) <= 0.5 <= (0.7 + 0.1)
 *
 * ```
 *  0.5 shouldBe between(0.1, 1.0, 0.0)     // Assertion passes
 *  0.5 shouldNotBe between(1.0, 2.0, 0.1)  // Assertion passes
 * ```
 *
 * @see [Double.shouldBeBetween]
 * @see [Double.shouldNotBeBetween]
 */
fun between(a: Double, b: Double, tolerance: Double): Matcher<Double> = object : Matcher<Double> {
   override fun test(value: Double): MatcherResult {
      val min = a - tolerance
      val max = b + tolerance
      return invoke(
         value in min..max,
         { "$value is outside expected range [$a, $b] (using tolerance $tolerance)" },
         { "$value is not outside expected range [$a, $b] (using tolerance $tolerance)" }
      )
   }
}
