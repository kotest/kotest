package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

infix fun <T, U> Iterable<T>.shouldBeLargerThan(other: Collection<U>): Iterable<T> {
   toList().shouldBeLargerThan(other)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeLargerThan(other: Iterable<U>): Iterable<T> {
   toList().shouldBeLargerThan(other.toList())
   return this
}

infix fun <T, U> Array<T>.shouldBeLargerThan(other: Collection<U>): Array<T> {
   asList().shouldBeLargerThan(other)
   return this
}

infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>): Array<T> {
   asList().shouldBeLargerThan(other.asList())
   return this
}

infix fun <T, U> Collection<T>.shouldBeLargerThan(other: Collection<U>): Collection<T> {
   this should beLargerThan(other)
   return this
}

fun <T, U> beLargerThan(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size > other.size,
      { "Collection of size ${value.size} should be larger than collection of size ${other.size}" },
      {
         "Collection of size ${value.size} should not be larger than collection of size ${other.size}"
      })
}
