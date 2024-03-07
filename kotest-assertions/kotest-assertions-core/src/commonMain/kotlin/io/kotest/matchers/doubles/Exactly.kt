package io.kotest.matchers.doubles

import io.kotest.assertions.AssertionsConfig
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

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
 *
 * This matcher considers NaN to be equal to NaN.
 * To disable this behavior, set the system property `kotest.assertions.nan.equality.disable` to true.
 */
infix fun Double.shouldBeExactly(other: Double): Double {
   if (AssertionsConfig.disableNaNEquality) {
      this shouldBe exactly(other)
   } else {
      this shouldBe exactlyByBits(other)
   }
   return this
}

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
 *
 * This matcher considers NaN to be equal to NaN.
 * To disable this behavior, set the system property `kotest.assertions.nan.equality.disable` to true.
 */
infix fun Double.shouldNotBeExactly(other: Double): Double {
   if (AssertionsConfig.disableNaNEquality) {
      this shouldNotBe exactly(other)
   } else {
      this shouldNotBe exactlyByBits(other)
   }
   return this
}

fun exactly(d: Double): Matcher<Double> = object : Matcher<Double> {
   override fun test(value: Double) =
      MatcherResult(
         value == d,
         { "$value is not equal to expected value $d" },
         { "$value should not equal $d" }
      )
}

fun exactlyByBits(expected: Double): Matcher<Double> = object : Matcher<Double> {
   override fun test(value: Double) =
      MatcherResult(
         value.toBits() == expected.toBits(),
         { "$value is not equal to expected value $expected" },
         { "$value should not equal $expected" }
      )
}
