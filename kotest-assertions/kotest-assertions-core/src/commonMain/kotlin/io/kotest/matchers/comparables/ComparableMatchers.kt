package io.kotest.matchers.comparables

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe

/**
 * Verifies that this is less than [other]
 *
 * Opposite of [shouldNotBeLessThan]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is less than [other] (compareTo returns < 0).
 *
 * @see [shouldNotBeLessThan]
 * @see [shouldBeLessThanOrEqualTo]
 */
infix fun <T : Comparable<T>> T.shouldBeLessThan(other: T): T {
   this shouldBe lt(other)
   return this
}

/**
 * Verifies that this is NOT less than [other]
 *
 * Opposite of [shouldBeLessThan]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is not less than [other] (compareTo doesn't return < 0).
 *
 * @see [shouldBeLessThan]
 * @see [shouldNotBeLessThanOrEqualTo]
 */
infix fun <T : Comparable<T>> T.shouldNotBeLessThan(other: T): T {
   this shouldNotBe lt(other)
   return this
}

fun <T : Comparable<T>> lt(x: T) = beLessThan(x)
fun <T : Comparable<T>> beLessThan(x: T) = object : Matcher<Comparable<T>> {
   override fun test(value: Comparable<T>) = MatcherResult.invoke(
      value < x,
      { "$value should be < $x" },
      { "$value should not be < $x" }
   )
}

/**
 * Verifies that this is less than or equal to [other]
 *
 * Opposite of [shouldNotBeLessThanOrEqualTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is less than or equal to [other] (compareTo returns <= 0).
 *
 * @see [shouldNotBeLessThanOrEqualTo]
 * @see [shouldBeLessThan]
 */
infix fun <T : Comparable<T>> T.shouldBeLessThanOrEqualTo(other: T): T {
   this shouldBe lte(other)
   return this
}

/**
 * Verifies that this is NOT less than nor equal to [other]
 *
 * Opposite of [shouldBeLessThanOrEqualTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is not less than nor equal to [other] (compareTo doesn't return <= 0).
 *
 * @see [shouldBeLessThanOrEqualTo]
 * @see [shouldNotBeLessThan]
 */
infix fun <T : Comparable<T>> T.shouldNotBeLessThanOrEqualTo(other: T): T {
   this shouldNotBe lte(other)
   return this
}

fun <T : Comparable<T>> lte(x: T) = beLessThanOrEqualTo(x)
fun <T : Comparable<T>> beLessThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
   override fun test(value: Comparable<T>) = MatcherResult.invoke(
      value <= x,
      { "$value should be <= $x" },
      { "$value should not be <= $x" }
   )
}

/**
 * Verifies that this is greater than [other]
 *
 * Opposite of [shouldNotBeGreaterThan]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is greater than [other] (compareTo returns > 0).
 *
 * @see [shouldNotBeGreaterThan]
 * @see [shouldBeGreaterThanOrEqualTo]
 */
infix fun <T : Comparable<T>> T.shouldBeGreaterThan(other: T): T {
   this shouldBe gt(other)
   return this
}

/**
 * Verifies that this is NOT greater than [other]
 *
 * Opposite of [shouldBeGreaterThan]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is NOT greater than [other] (compareTo doesn't return > 0).
 *
 * @see [shouldBeGreaterThan]
 * @see [shouldNotBeGreaterThanOrEqualTo]
 */
infix fun <T : Comparable<T>> T.shouldNotBeGreaterThan(other: T) = this shouldNotBe gt(other)
fun <T : Comparable<T>> gt(x: T) = beGreaterThan(x)
fun <T : Comparable<T>> beGreaterThan(x: T) = object : Matcher<Comparable<T>> {
   override fun test(value: Comparable<T>) = MatcherResult.invoke(
      value > x,
      { "$value should be > $x" },
      { "$value should not be > $x" }
   )
}

/**
 * Verifies that this is greater than or equal to [other]
 *
 * Opposite of [shouldNotBeGreaterThanOrEqualTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is greater than or equal to [other] (compareTo returns >= 0).
 *
 * @see [shouldNotBeGreaterThanOrEqualTo]
 * @see [shouldBeGreaterThan]
 */
infix fun <T : Comparable<T>> T.shouldBeGreaterThanOrEqualTo(other: T): T {
   this shouldBe gte(other)
   return this
}

/**
 * Verifies that this is NOT greater than nor equal to [other]
 *
 * Opposite of [shouldBeGreaterThanOrEqualTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is NOT greater than nor equal to [other] (compareTo doesn't return >= 0).
 *
 * @see [shouldBeGreaterThanOrEqualTo]
 * @see [shouldNotBeGreaterThan]
 */
infix fun <T : Comparable<T>> T.shouldNotBeGreaterThanOrEqualTo(other: T): T {
   this shouldNotBe gte(other)
   return this
}

fun <T : Comparable<T>> gte(x: T) = beGreaterThanOrEqualTo(x)
fun <T : Comparable<T>> beGreaterThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
   override fun test(value: Comparable<T>) = MatcherResult.invoke(
      value >= x,
      { "$value should be >= $x" },
      { "$value should not be >= $x" }
   )
}

/**
 * Verifies that this is equal to [other] using compareTo
 *
 * Opposite of [shouldNotBeEqualComparingTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is equal to [other] (compareTo returns 0).
 *
 */
infix fun <T : Comparable<T>> T.shouldBeEqualComparingTo(other: T): T {
   this should beEqualComparingTo(other)
   return this
}

/**
 * Verifies that this is NOT equal to [other] using compareTo
 *
 * Opposite of [shouldBeEqualComparingTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is NOT equal to [other] (compareTo doesn't return 0).
 *
 */
infix fun <T : Comparable<T>> T.shouldNotBeEqualComparingTo(other: T): T {
   this shouldNot beEqualComparingTo(other)
   return this
}

fun <T : Comparable<T>> beEqualComparingTo(other: T) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val passed = value.compareTo(other) == 0
      return MatcherResult.invoke(
         passed,
         { "Value $value should compare equal to $other" },
         { "Value $value should not compare equal to $other" })
   }
}

/**
 * Verifies that this is equal to [other] using compare from [comparator]
 *
 *
 * This function will check for the result of [comparator.compare][Comparator.compare] and result accordingly.
 * This will pass if the value is equal to [other] (compare returns 0).
 *
 */
fun <T : Comparable<T>> T.shouldBeEqualComparingTo(other: T, comparator: Comparator<T>): T {
   this should compareTo(other, comparator)
   return this
}

/**
 * Verifies that this is NOT equal to [other] using compare from [comparator]
 *
 *
 * This function will check for the result of [comparator.compare][Comparator.compare] and result accordingly.
 * This will pass if the value is NOT equal to [other] (compare doesn't return 0).
 *
 */
fun <T : Comparable<T>> T.shouldNotBeEqualComparingTo(other: T, comparator: Comparator<T>): T {
   this shouldNot compareTo(other, comparator)
   return this
}

fun <T> compareTo(other: T, comparator: Comparator<T>) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val passed = comparator.compare(value, other) == 0
      return MatcherResult.invoke(
         passed,
         { "Value $value should compare equal to $other" },
         { "Value $value should not compare equal to $other" })
   }
}

/**
 * Verifies that `this` is in the closed interval `[lower, upper]` (inclusive, inclusive).
 *
 * This assertion always fails if the supplied `lower > upper`.
 */
fun <T : Comparable<T>> T.shouldBeBetween(lower: T, upper: T): T {
   this should beBetween(lower, upper)
   return this
}

/**
 * Verifies that `this` is outside the closed interval `[lower, upper]`  (inclusive, inclusive).
 *
 * This assertion always succeeds if the supplied `lower > upper`.
 */
fun <T : Comparable<T>> T.shouldNotBeBetween(lower: T, upper: T): T {
   this shouldNot beBetween(lower, upper)
   return this
}

/**
 * Match that verifies a given [T] has a value between [lower, upper] (inclusive, inclusive).
 */
fun <T : Comparable<T>> between(lower: T, upper: T): Matcher<T> = beBetween(lower, upper)

/**
 * Match that verifies a given [T] has a value between [lower, upper] (inclusive, inclusive).
 */
fun <T : Comparable<T>> beBetween(lower: T, upper: T): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult = MatcherResult(
      value in lower..upper,
      { "${value.print().value} should be between (${lower.print().value}, ${upper.print().value}) inclusive" },
      { "${value.print().value} should not be between (${lower.print().value}, ${upper.print().value}) inclusive" },
   )
}
