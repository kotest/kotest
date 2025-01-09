package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.sequences.beLargerThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


/**
 * Asserts that this [Iterable] is larger than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains more elements than the
 * specified [other] collection.
 *
 * Opposite of [Iterable.shouldNotBeLargerThan].
 *
 * ```
 * listOf(1, 2, 3) shouldBeLargerThan listOf(1, 2)    // Assertion passes
 * listOf(1) shouldBeLargerThan listOf(1, 2, 3)       // Assertion fails
 * ```
 *
 * @see Iterable.shouldNotBeLargerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldBeLargerThan(other: Iterable<U>): I = apply {
   toList() should beLargerThan(other)
}

/**
 * Asserts that this [Iterable] is NOT larger than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection does not contain more elements than
 * the specified [other] collection.
 *
 * Opposite of [Iterable.shouldBeLargerThan].
 *
 * ```
 * listOf(1) shouldNotBeLargerThan listOf(1, 2, 3)       // Assertion passes
 * listOf(1, 2, 3) shouldNotBeLargerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeLargerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldNotBeLargerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beLargerThan(other)
}

/**
 * Asserts that this [Array] is larger than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains more elements than the specified
 * [other] array.
 *
 * Opposite of [Array.shouldNotBeLargerThan].
 *
 * ```
 * arrayOf(1, 2, 3) shouldBeLargerThan arrayOf(1, 2)    // Assertion passes
 * arrayOf(1) shouldBeLargerThan arrayOf(1, 2, 3)       // Assertion fails
 * ```
 *
 * @see Array.shouldNotBeLargerThan
 */
infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldBeLargerThan(other.asList())
}

/**
 * Asserts that this [Array] is NOT larger than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array does not contain more elements than the
 * specified [other] array.
 *
 * Opposite of [Array.shouldBeLargerThan].
 *
 * ```
 * arrayOf(1) shouldNotBeLargerThan arrayOf(1, 2, 3)         // Assertion passes
 * arrayOf(1, 2, 3) shouldNotBeLargerThan arrayOf(1, 2)      // Assertion fails
 * ```
 *
 * @see Array.shouldBeLargerThan
 */
infix fun <T, U> Array<T>.shouldNotBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldNotBeLargerThan(other.asList())
}

/**
 * Matcher that asserts the size of an [Iterable] is larger than the size of [other].
 *
 * This matcher returns a successful result if the number of elements in the tested [Iterable]
 * is strictly greater than the number of elements in [other].
 *
 * Example:
 * ```
 * listOf(1, 2, 3) should beLargerThan(listOf(1, 2))    // Assertion passes
 * listOf(1) shouldNot beLargerThan(listOf(1, 2, 3))    // Assertion passes
 * ```
 *
 * @param other the [Iterable] to compare against.
 * @return a [Matcher] that validates the size relationship.
 * @see Iterable.shouldBeLargerThan
 * @see Iterable.shouldNotBeLargerThan
 */
fun <T, U> beLargerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   val otherSize = other.count()
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() > otherSize,
      { "Collection of size ${value.count()} should be larger than collection of size $otherSize" },
      { "Collection of size ${value.count()} should not be larger than collection of size $otherSize" }
   )
}
