package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


/**
 * Verifies that this instance is in [collection]
 *
 * Assertion to check that this instance is in [collection]. This assertion checks by reference, and not by value,
 * therefore the exact instance must be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
infix fun <T> T.shouldBeOneOf(collection: Collection<T>): T {
   this should beOneOf(collection)
   return this
}

/**
 * Verifies that this instance is NOT in [collection]
 *
 * Assertion to check that this instance is not in [collection]. This assertion checks by reference, and not by value,
 * therefore the exact instance must not be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldBeOneOf]
 * @see [beOneOf]
 */
infix fun <T> T.shouldNotBeOneOf(collection: Collection<T>): T {
   this shouldNot beOneOf(collection)
   return this
}

/**
 * Verifies that this instance is any of [any]
 *
 * Assertion to check that this instance is any of [any]. This assertion checks by reference, and not by value,
 * therefore the exact instance must be in [any], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
fun <T> T.shouldBeOneOf(vararg any: T): T {
   this should beOneOf(any.toList())
   return this
}

/**
 * Verifies that this instance is NOT any of [any]
 *
 * Assertion to check that this instance is not any of [any]. This assertion checks by reference, and not by value,
 * therefore the exact instance must not be in [any], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldNotBeOneOf]
 * @see [beOneOf]
 */
fun <T> T.shouldNotBeOneOf(vararg any: T): T {
   this shouldNot beOneOf(any.toList())
   return this
}

/**
 * Matcher that verifies that this instance is in [collection]
 *
 * Assertion to check that this instance is in [collection]. This matcher checks by reference, and not by value,
 * therefore the exact instance must be in [collection], or this will fail.
 *
 * An empty collection will always fail. If you need to check for empty collection, use [Collection.shouldBeEmpty]
 *
 * @see [shouldBeOneOf]
 * @see [shouldNotBeOneOf]
 */
fun <T> beOneOf(collection: Collection<T>) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      if (collection.isEmpty()) throwEmptyCollectionError()
      val match = collection.any { it === value }
      val indexesOfEqualElementsDescription = {
         if (match) "" else {
            val indexes = collection.mapIndexedNotNull { index, it -> if (it == value) index else null }
            if (indexes.isNotEmpty()) {
               "\nFound equal but not the same element(s) at index(es): ${indexes.print().value}"
            } else ""
         }
      }
      return MatcherResult(
         match,
         { "Collection should contain the instance ${value.print().value} with hashcode ${value.hashCode()}.${indexesOfEqualElementsDescription()}" },
         { "Collection should not contain the instance ${value.print().value} with hashcode ${value.hashCode()}." })
   }
}
