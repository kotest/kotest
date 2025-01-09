package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that this [Iterable] is smaller than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains fewer elements than the
 * specified [other] collection.
 *
 * Opposite of [Iterable.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * listOf(1, 2) shouldBeSmallerThan listOf(1, 2, 3)    // Assertion passes
 * listOf(1, 2, 3) shouldBeSmallerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldNotBeSmallerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldBeSmallerThan(other: Iterable<U>): I = apply {
   toList() should beSmallerThan(other)
}

/**
 * Asserts that this [Array] is smaller than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains fewer elements than the specified
 * [other] array.
 *
 * Opposite of [Array.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * arrayOf(1, 2) shouldBeSmallerThan arrayOf(1, 2, 3)    // Assertion passes
 * arrayOf(1, 2, 3) shouldBeSmallerThan arrayOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Array.shouldNotBeSmallerThan
 */
infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() should beSmallerThan(other.asList())
}


/**
 * Asserts that this [Iterable] is NOT smaller than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains at least as many elements as
 * the specified [other] collection.
 *
 * Opposite of [Iterable.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * listOf(1, 2, 3) shouldNotBeSmallerThan listOf(1, 2)    // Assertion passes
 * listOf(1, 2) shouldNotBeSmallerThan listOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeSmallerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldNotBeSmallerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beSmallerThan(other)
}

/**
 * Asserts that this [Array] is NOT smaller than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [Array.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * arrayOf(1, 2, 3) shouldNotBeSmallerThan arrayOf(1, 2)    // Assertion passes
 * arrayOf(1, 2) shouldNotBeSmallerThan arrayOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see Array.shouldBeSmallerThan
 */
infix fun <T, U> Array<T>.shouldNotBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() shouldNot beSmallerThan(other.asList())
}



/**
 * Matcher that verifies a collection is smaller than [other].
 *
 * Compares the sizes of two collections and returns a positive result if the first collection contains fewer elements
 * than the second.
 *
 * Example:
 * ```
 * listOf(1, 2) should beSmallerThan listOf(1, 2, 3)    // Assertion passes
 * listOf(1, 2, 3) should beSmallerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeSmallerThan
 * @see Iterable.shouldNotBeSmallerThan
 */
fun <T, U> beSmallerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() < other.count(),
      { "Collection of size ${value.count()} should be smaller than collection of size ${other.count()}" },
      { "Collection of size ${value.count()} should not be smaller than collection of size ${other.count()}" }
   )
}
