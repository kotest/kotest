package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T, U, I : Iterable<T>> I.shouldBeSmallerThan(other: Collection<U>): I {
   toList().shouldBeSmallerThan(other)
   return this
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Collection<U>): Array<T> {
   asList().shouldBeSmallerThan(other)
   return this
}

infix fun <T, U, I : Iterable<T>> I.shouldBeSmallerThan(other: Iterable<U>): I {
   toList().shouldBeSmallerThan(other.toList())
   return this
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>): Array<T> {
   asList().shouldBeSmallerThan(other.asList())
   return this
}

infix fun <T, U, C : Collection<T>> C.shouldBeSmallerThan(other: Collection<U>): C {
   this should beSmallerThan(other)
   return this
}

fun <T, U> beSmallerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size < other.size,
      { "Collection of size ${value.size} should be smaller than collection of size ${other.size}" },
      { "Collection of size ${value.size} should not be smaller than collection of size ${other.size}" })
}
