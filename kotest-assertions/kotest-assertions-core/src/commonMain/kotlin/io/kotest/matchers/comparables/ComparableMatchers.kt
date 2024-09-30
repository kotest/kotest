package io.kotest.matchers.comparables

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResult.Companion.invoke
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
infix fun <T : Comparable<T>> T.shouldBeLessThan(other: T) = this shouldBe lt(other)

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
infix fun <T : Comparable<T>> T.shouldNotBeLessThan(other: T) = this shouldNotBe lt(other)
fun <T : Comparable<T>> lt(x: T) = beLessThan(x)
fun <T : Comparable<T>> beLessThan(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = invoke(
     value < x,
     { "$value should be < $x" },
     { "$value should not be < $x" })
}

/**
 * Verifies that this is less than or equal[other]
 *
 * Opposite of [shouldNotBeLessThanOrEqualTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is less than or equal to [other] (compareTo returns <= 0).
 *
 * @see [shouldNotBeLessThanOrEqualTo]
 * @see [shouldBeLessThan]
 */
infix fun <T : Comparable<T>> T.shouldBeLessThanOrEqualTo(other: T) = this shouldBe lte(other)
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
infix fun <T : Comparable<T>> T.shouldNotBeLessThanOrEqualTo(other: T) = this shouldNotBe lte(other)
fun <T : Comparable<T>> lte(x: T) = beLessThanOrEqualTo(x)
fun <T : Comparable<T>> beLessThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = invoke(
     value <= x,
     { "$value should be <= $x" },
     { "$value should not be <= $x" })
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
infix fun <T : Comparable<T>> T.shouldBeGreaterThan(other: T) = this shouldBe gt(other)
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
  override fun test(value: Comparable<T>) = invoke(
     value > x,
     { "$value should be > $x" },
     { "$value should not be > $x" })
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
infix fun <T : Comparable<T>> T.shouldBeGreaterThanOrEqualTo(other: T) = this shouldBe gte(other)
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
infix fun <T : Comparable<T>> T.shouldNotBeGreaterThanOrEqualTo(other: T) = this shouldNotBe gte(other)
fun <T : Comparable<T>> gte(x: T) = beGreaterThanOrEqualTo(x)
fun <T : Comparable<T>> beGreaterThanOrEqualTo(x: T) = object : Matcher<Comparable<T>> {
  override fun test(value: Comparable<T>) = invoke(
     value >= x,
     { "$value should be >= $x" },
     { "$value should not be >= $x" })
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
infix fun <T : Comparable<T>> T.shouldBeEqualComparingTo(other: T) = this should beEqualComparingTo(other)
/**
 * Verifies that this is NOT equal to [other] using compareTo
 *
 * Opposite of [shouldBeEqualComparingTo]
 *
 * This function will check for the result of [Comparable.compareTo] and result accordingly.
 * This will pass if the value is NOT equal to [other] (compareTo doesn't return 0).
 *
 */
infix fun <T : Comparable<T>> T.shouldNotBeEqualComparingTo(other: T) = this shouldNot beEqualComparingTo(other)
fun <T : Comparable<T>> beEqualComparingTo(other: T) = object : Matcher<T> {
  override fun test(value: T): MatcherResult {
    val passed = value.compareTo(other) == 0
    return invoke(
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
fun <T : Comparable<T>> T.shouldBeEqualComparingTo(other: T, comparator: Comparator<T>) = this should compareTo(other, comparator)
/**
 * Verifies that this is NOT equal to [other] using compare from [comparator]
 *
 *
 * This function will check for the result of [comparator.compare][Comparator.compare] and result accordingly.
 * This will pass if the value is NOT equal to [other] (compare doesn't return 0).
 *
 */
fun <T : Comparable<T>> T.shouldNotBeEqualComparingTo(other: T, comparator: Comparator<T>) = this shouldNot compareTo(other, comparator)
fun <T> compareTo(other: T, comparator: Comparator<T>) = object : Matcher<T> {
  override fun test(value: T): MatcherResult {
    val passed = comparator.compare(value, other) == 0
    return invoke(
       passed,
       { "Value $value should compare equal to $other" },
       { "Value $value should not compare equal to $other" })
  }
}
