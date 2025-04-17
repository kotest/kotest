package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that this iterable contains only null elements.
 *
 * Verifies that all elements in the iterable are null.
 *
 * Opposite of [shouldNotContainOnlyNulls].
 *
 * Example:
 * ```
 * listOf(null, null).shouldContainOnlyNulls() // Assertion passes
 * listOf(null, "a").shouldContainOnlyNulls()  // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldContainOnlyNulls(): I = apply { toList() should containOnlyNulls() }

/**
 * Asserts that this array contains only null elements.
 *
 * Verifies that all elements in the array are null.
 *
 * Opposite of [shouldNotContainOnlyNulls].
 *
 * Example:
 * ```
 * arrayOf(null, null).shouldContainOnlyNulls() // Assertion passes
 * arrayOf(null, "a").shouldContainOnlyNulls()  // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldContainOnlyNulls(): Array<T> = apply { asList().shouldContainOnlyNulls() }

/**
 * Asserts that this iterable does not contain only null elements.
 *
 * Verifies that the iterable contains at least one non-null element.
 *
 * Opposite of [shouldContainOnlyNulls].
 *
 * Example:
 * ```
 * listOf(null, "a").shouldNotContainOnlyNulls()  // Assertion passes
 * listOf(null, null).shouldNotContainOnlyNulls() // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldNotContainOnlyNulls(): I = apply { toList() shouldNot containOnlyNulls() }

/**
 * Asserts that this array does not contain only null elements.
 *
 * Verifies that the array contains at least one non-null element.
 *
 * Opposite of [shouldContainOnlyNulls].
 *
 * Example:
 * ```
 * arrayOf(null, "a").shouldNotContainOnlyNulls()  // Assertion passes
 * arrayOf(null, null).shouldNotContainOnlyNulls() // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldNotContainOnlyNulls(): Array<T> = apply { asList().shouldNotContainOnlyNulls() }

/**
 * Matcher that asserts a collection contains only null elements.
 *
 * Verifies that all elements in the collection are null.
 *
 * Example:
 * ```
 * listOf(null, null) should containOnlyNulls() // Assertion passes
 * listOf(null, "a") should containOnlyNulls()  // Assertion fails
 * ```
 *
 * Example with `shouldNot`:
 * ```
 * listOf("a", null) shouldNot containOnlyNulls()  // Assertion passes
 * listOf(null, null) shouldNot containOnlyNulls() // Assertion fails
 * ```
 *
 * @return a Matcher for asserting the collection contains only null elements
 */
fun <T> containOnlyNulls() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.all { it == null },
         { "Collection should contain only nulls" },
         { "Collection should not contain only nulls" }
      )
}


/**
 * Asserts that this iterable contains at least one null element.
 *
 * Verifies that the iterable has at least one null element.
 *
 * Opposite of [shouldNotContainNull].
 *
 * Example:
 * ```
 * listOf(null, "a").shouldContainNull() // Assertion passes
 * listOf("a", "b").shouldContainNull()  // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldContainNull(): I = apply { toList() should containNull() }

/**
 * Asserts that this array contains at least one null element.
 *
 * Verifies that the array has at least one null element.
 *
 * Opposite of [shouldNotContainNull].
 *
 * Example:
 * ```
 * arrayOf(null, "a").shouldContainNull() // Assertion passes
 * arrayOf("a", "b").shouldContainNull()  // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldContainNull(): Array<T> = apply { asList().shouldContainNull() }


/**
 * Asserts that this iterable does not contain any null elements.
 *
 * Verifies that the iterable contains no null elements.
 *
 * Opposite of [shouldContainNull].
 *
 * Example:
 * ```
 * listOf("a", "b").shouldNotContainNull()  // Assertion passes
 * listOf("a", null).shouldNotContainNull() // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldNotContainNull(): I = apply { toList() shouldNot containNull() }

/**
 * Asserts that this array does not contain any null elements.
 *
 * Verifies that the array contains no null elements.
 *
 * Opposite of [shouldContainNull].
 *
 * Example:
 * ```
 * arrayOf("a", "b").shouldNotContainNull()  // Assertion passes
 * arrayOf("a", null).shouldNotContainNull() // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldNotContainNull(): Array<T> = apply { asList().shouldNotContainNull() }

/**
 * Matcher that asserts a collection contains at least one null element.
 *
 * Verifies that the collection has at least one null element.
 *
 * Example:
 * ```
 * listOf(null, "a") should containNull() // Assertion passes
 * listOf("a", "b") should containNull()  // Assertion fails
 * ```
 *
 * Example with `shouldNot`:
 * ```
 * listOf("a", "b") shouldNot containNull()  // Assertion passes
 * listOf(null, "a") shouldNot containNull() // Assertion fails
 * ```
 *
 * @return a Matcher for asserting the collection contains at least one null element
 */
fun <T> containNull() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.any { it == null },
         { "Collection should contain at least one null" },
         { "Collection should not contain any nulls" }
      )
}


/**
 * Asserts that this iterable contains no null elements.
 *
 * Verifies that all elements in the iterable are non-null.
 *
 * Opposite of [shouldNotContainNoNulls].
 *
 * Example:
 * ```
 * listOf("a", "b").shouldContainNoNulls()  // Assertion passes
 * listOf("a", null).shouldContainNoNulls() // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldContainNoNulls(): I = apply { toList() should containNoNulls() }

/**
 * Asserts that this array contains no null elements.
 *
 * Verifies that all elements in the array are non-null.
 *
 * Opposite of [shouldNotContainNoNulls].
 *
 * Example:
 * ```
 * arrayOf("a", "b").shouldContainNoNulls()  // Assertion passes
 * arrayOf("a", null).shouldContainNoNulls() // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldContainNoNulls(): Array<T> = apply { asList().shouldContainNoNulls() }


/**
 * Asserts that this iterable does not contain exclusively non-null elements.
 *
 * Verifies that the iterable contains at least one null element.
 *
 * Opposite of [shouldContainNoNulls].
 *
 * Example:
 * ```
 * listOf("a", null).shouldNotContainNoNulls() // Assertion passes
 * listOf("a", "b").shouldNotContainNoNulls()  // Assertion fails
 * ```
 *
 * @return the same iterable for further assertions
 */
fun <T, I : Iterable<T>> I.shouldNotContainNoNulls(): I = apply { toList() shouldNot containNoNulls() }

/**
 * Asserts that this array does not contain exclusively non-null elements.
 *
 * Verifies that the array contains at least one null element.
 *
 * Opposite of [shouldContainNoNulls].
 *
 * Example:
 * ```
 * arrayOf("a", null).shouldNotContainNoNulls() // Assertion passes
 * arrayOf("a", "b").shouldNotContainNoNulls()  // Assertion fails
 * ```
 *
 * @return the same array for further assertions
 */
fun <T> Array<T>.shouldNotContainNoNulls(): Array<T> = apply { asList().shouldNotContainNoNulls() }

/**
 * Matcher that asserts a collection contains no null elements.
 *
 * Verifies that all elements in the collection are non-null.
 *
 * Example:
 * ```
 * listOf("a", "b") should containNoNulls()  // Assertion passes
 * listOf("a", null) should containNoNulls() // Assertion fails
 * ```
 *
 * Example with `shouldNot`:
 * ```
 * listOf("a", null) shouldNot containNoNulls() // Assertion passes
 * listOf("a", "b") shouldNot containNoNulls()  // Assertion fails
 * ```
 *
 * @return a Matcher for asserting the collection contains no null elements
 */
fun <T> containNoNulls() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.all { it != null },
         { "Collection should not contain nulls" },
         { "Collection should have at least one null" }
      )
}

