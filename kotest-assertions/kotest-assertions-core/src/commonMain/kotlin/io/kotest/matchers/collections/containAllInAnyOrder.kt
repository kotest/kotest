@file:Suppress("unused")

package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.sequences.UnorderedCollectionsDifference
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T, C : Collection<T>> C?.shouldNotContainAllInAnyOrder(expected: C) =
   this shouldNot containAllInAnyOrder(expected)

fun <T, C : Collection<T>> C?.shouldNotContainAllInAnyOrder(vararg expected: T) =
   this shouldNot containAllInAnyOrder(*expected)

/**
 * Verifies that the given [Collection] contains all the specified elements in any order.
 * The collection may additionally contain other elements.
 */
infix fun <T, C : Collection<T>> C?.shouldContainAllInAnyOrder(expected: C) =
   this should containAllInAnyOrder(expected)

/**
 * Verifies that the given [Collection] contains all the specified elements in any order.
 * The collection may additionally contain other elements.
 */
fun <T, C : Collection<T>> C?.shouldContainAllInAnyOrder(vararg expected: T) =
   this should containAllInAnyOrder(*expected)

/**
 * Verifies that the given [Collection] contains all the specified elements in any order.
 * The collection may additionally contain other elements.
 */
fun <T> containAllInAnyOrder(vararg expected: T): Matcher<Collection<T>?> =
   containAllInAnyOrder(expected.asList())

/**
 * Verifies that the given [Collection] contains all the specified elements in any order.
 * The collection may additionally contain other elements.
 */
fun <T, C : Collection<T>> containAllInAnyOrder(expected: C): Matcher<C?> = neverNullMatcher { value ->
   val expectedAsList = expected.toList()
   val actualAsList = value.toList()
   val comparison = UnorderedCollectionsDifference.matchIgnoringMissingElements(expectedAsList, actualAsList)
   MatcherResult(
      comparison.isMatch(),
      { "Collection should contain the values of $expectedAsList in any order, but was $actualAsList.${comparison}" },
      { "Collection should not contain the values of $expectedAsList in any order" }
   )
}
