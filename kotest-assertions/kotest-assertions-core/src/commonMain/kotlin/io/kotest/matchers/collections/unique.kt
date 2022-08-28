package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that the given [Iterable] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining, maintaining the input type
 */
fun <E, T : Iterable<E>> T.shouldBeUnique(): T {
   toList() should beUnique()
   return this
}

/**
 * Asserts that the given [Iterable] contains no duplicate elements using the given
 * [comparator] for equality.
 *
 * @return the input instance is returned for chaining, maintaining the input type
 */
fun <E, T : Iterable<E>> T.shouldBeUnique(comparator: Comparator<E>): T {
   toList() should beUnique(comparator)
   return this
}

/**
 * Asserts that the given [Array] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun <T> Array<T>.shouldBeUnique(): Array<T> {
   asList().shouldBeUnique()
   return this
}

/**
 * Asserts that the given [Array] contains no duplicate elements using the given
 * [comparator] for equality.
 *
 * @return the input instance is returned for chaining
 */
fun <T> Array<T>.shouldBeUnique(comparator: Comparator<T>): Array<T> {
   asList().shouldBeUnique(comparator)
   return this
}

fun <T> Iterable<T>.shouldNotBeUnique(): Iterable<T> {
   toList().shouldNotBeUnique()
   return this
}

fun <T> Array<T>.shouldNotBeUnique(): Array<T> {
   asList().shouldNotBeUnique()
   return this
}

fun <T> Collection<T>.shouldNotBeUnique(): Collection<T> {
   this shouldNot beUnique()
   return this
}

fun <T> beUnique() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {

      val list = value.toMutableList()
      list.toSet().forEach { list.remove(it) }
      val duplicates = list.toList().distinct()

      return MatcherResult(
         duplicates.isEmpty(),
         { "Collection should be unique but contained duplicates of ${duplicates.joinToString(", ")}" },
         { "Collection should contain at least one duplicate element" })
   }
}

fun <T> beUnique(comparator: Comparator<T>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {

      val duplicates = value.toList()
         .sortedWith(comparator)
         .windowed(2)
         .find { comparator.compare(it.first(), it.last()) == 0 }

      return MatcherResult(
         duplicates == null,
         { "Collection should be unique but contained duplicates of ${duplicates?.first()}" },
         { "Collection should contain at least one duplicate element" })
   }
}
