package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T, U> Iterable<T>.shouldBeSmallerThan(other: Collection<U>): Iterable<T> {
   toList().shouldBeSmallerThan(other)
   return this
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Collection<U>): Array<T> {
   asList().shouldBeSmallerThan(other)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeSmallerThan(other: Iterable<U>): Iterable<T> {
   toList().shouldBeSmallerThan(other.toList())
   return this
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>): Array<T> {
   asList().shouldBeSmallerThan(other.asList())
   return this
}

infix fun <T, U> Collection<T>.shouldBeSmallerThan(other: Collection<U>): Collection<T> {
   this should beSmallerThan(other)
   return this
}

fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size < other.size,
      { "Collection of size ${value.size} should be smaller than collection of size ${other.size}" },
      { "Collection of size ${value.size} should not be smaller than collection of size ${other.size}" })
}
