package io.kotest.matchers.floats

import io.kotest.assertions.AssertionsConfig
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlin.math.absoluteValue

fun exactly(d: Float): Matcher<Float> = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value == d,
         { "$value is not equal to expected value $d" },
         { "$value should not be equal to $d" }
      )
}

fun exactlyByBits(expected: Float): Matcher<Float> = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value.toBits() == expected.toBits(),
         { "$value is not equal to expected value $expected" },
         { "$value should not equal $expected" }
      )
}

fun lt(x: Float) = beLessThan(x)
fun beLessThan(x: Float) = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value < x,
         { "$value should be < $x" },
         { "$value should not be < $x" })
}

fun lte(x: Float) = beLessThanOrEqualTo(x)
fun beLessThanOrEqualTo(x: Float) = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value <= x,
         { "$value should be <= $x" },
         { "$value should not be <= $x" })
}

fun gt(x: Float) = beGreaterThan(x)
fun beGreaterThan(x: Float) = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value > x,
         { "$value should be > $x" },
         { "$value should not be > $x" })
}

fun gte(x: Float) = beGreaterThanOrEqualTo(x)
fun beGreaterThanOrEqualTo(x: Float) = object : Matcher<Float> {
   override fun test(value: Float) =
      MatcherResult(
         value >= x,
         { "$value should be >= $x" },
         { "$value should not be >= $x" })
}

infix fun Float.shouldBeLessThan(x: Float) = this shouldBe lt(x)
infix fun Float.shouldNotBeLessThan(x: Float) = this shouldNotBe lt(x)

infix fun Float.shouldBeLessThanOrEqual(x: Float) = this shouldBe lte(x)
infix fun Float.shouldNotBeLessThanOrEqual(x: Float) = this shouldNotBe lte(x)

infix fun Float.shouldBeGreaterThan(x: Float) = this shouldBe gt(x)
infix fun Float.shouldNotBeGreaterThan(x: Float) = this shouldNotBe gt(x)

infix fun Float.shouldBeGreaterThanOrEqual(x: Float) = this shouldBe gte(x)
infix fun Float.shouldNotBeGreaterThanOrEqual(x: Float) = this shouldNotBe gte(x)

infix fun Float.shouldBeExactly(x: Float): Float {
   if (AssertionsConfig.disableNaNEquality) {
      this shouldBe exactly(x)
   } else {
      this shouldBe exactlyByBits(x)
   }
   return this
}

infix fun Float.shouldNotBeExactly(x: Float): Float {
   if (AssertionsConfig.disableNaNEquality) {
      this shouldNotBe exactly(x)
   } else {
      this shouldNotBe exactlyByBits(x)
   }
   return this
}

fun Float.shouldBeZero() = this shouldBeExactly 0f
fun Float.shouldNotBeZero() = this shouldNotBeExactly 0f


/**
 * Verifies that this float is within [percentage]% of [other]
 *
 * 90.0.shouldBeWithinPercentageOf(100.0, 10.0)  // Passes
 * 50.0.shouldBeWithinPercentageOf(100.0, 50.0)  // Passes
 * 30.0.shouldBeWithinPercentageOf(100.0, 10.0)  // Fail
 *
 */
fun Float.shouldBeWithinPercentageOf(other: Float, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this should beWithinPercentageOf(other, percentage)
}

/**
 * Verifies that this float is NOT within [percentage]% of [other]
 *
 * 90.0.shouldNotBeWithinPercentageOf(100.0, 10.0)  // Fail
 * 50.0.shouldNotBeWithinPercentageOf(100.0, 50.0)  // Fail
 * 30.0.shouldNotBeWithinPercentageOf(100.0, 10.0)  // Passes
 *
 */
fun Float.shouldNotBeWithinPercentageOf(other: Float, percentage: Double) {
   require(percentage > 0.0) { "Percentage must be > 0.0" }
   this shouldNot beWithinPercentageOf(other, percentage)
}

fun beWithinPercentageOf(other: Float, percentage: Double) = object : Matcher<Float> {
   private val tolerance = other.times(percentage / 100).absoluteValue.toFloat()
   private val range = (other - tolerance)..(other + tolerance)

   override fun test(value: Float) = MatcherResult(
      value in range,
      { "$value should be in $range" },
      { "$value should not be in $range" })
}
