package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T, U, I : Iterable<T>> I.shouldBeSmallerThan(other: Iterable<U>): I = apply {
   toList() should beSmallerThan(other)
}

infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() should beSmallerThan(other.asList())
}

infix fun <T, U, I : Iterable<T>> I.shouldNotBeSmallerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beSmallerThan(other)
}

infix fun <T, U> Array<T>.shouldNotBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() shouldNot beSmallerThan(other.asList())
}


fun <T, U> beSmallerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() < other.count(),
      { "Collection of size ${value.count()} should be smaller than collection of size ${other.count()}" },
      { "Collection of size ${value.count()} should not be smaller than collection of size ${other.count()}" })
}
