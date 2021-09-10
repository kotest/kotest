package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldHaveSize(size: Int): Iterable<T> {
   toList().shouldHaveSize(size)
   return this
}

infix fun <T> Array<T>.shouldHaveSize(size: Int): Array<T> {
   asList().shouldHaveSize(size)
   return this
}

infix fun <T> Collection<T>.shouldHaveSize(size: Int): Collection<T> {
   this should haveSize(size = size)
   return this
}

infix fun <T> Iterable<T>.shouldNotHaveSize(size: Int): Iterable<T> {
   toList().shouldNotHaveSize(size)
   return this
}

infix fun <T> Array<T>.shouldNotHaveSize(size: Int): Array<T> {
   asList().shouldNotHaveSize(size)
   return this
}

infix fun <T> Collection<T>.shouldNotHaveSize(size: Int): Collection<T> {
   this shouldNot haveSize(size)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Collection<U>): Iterable<T> {
   toList().shouldBeSameSizeAs(other)
   return this
}

infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Collection<U>): Array<T> {
   asList().shouldBeSameSizeAs(other)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Iterable<U>): Iterable<T> {
   toList().shouldBeSameSizeAs(other.toList())
   return this
}

infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Array<U>): Array<T> {
   asList().shouldBeSameSizeAs(other.asList())
   return this
}

infix fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>): Collection<T> {
   this should beSameSizeAs(other)
   return this
}

fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size == other.size,
      { "Collection of size ${value.size} should be the same size as collection of size ${other.size}" },
      { "Collection of size ${value.size} should not be the same size as collection of size ${other.size}" })
}

infix fun <T> Iterable<T>.shouldHaveAtLeastSize(n: Int): Iterable<T> {
   toList().shouldHaveAtLeastSize(n)
   return this
}

infix fun <T> Array<T>.shouldHaveAtLeastSize(n: Int): Array<T> {
   asList().shouldHaveAtLeastSize(n)
   return this
}

infix fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int): Collection<T> {
   this shouldHave atLeastSize(n)
   return this
}

fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size >= n,
      { "Collection should contain at least $n elements" },
      {
         "Collection should contain less than $n elements"
      })
}

infix fun <T> Iterable<T>.shouldHaveAtMostSize(n: Int): Iterable<T> {
   toList().shouldHaveAtMostSize(n)
   return this
}

infix fun <T> Array<T>.shouldHaveAtMostSize(n: Int): Array<T> {
   asList().shouldHaveAtMostSize(n)
   return this
}

infix fun <T> Collection<T>.shouldHaveAtMostSize(n: Int): Collection<T> {
   this shouldHave atMostSize(n)
   return this
}

fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size <= n,
      { "Collection should contain at most $n elements" },
      {
         "Collection should contain more than $n elements"
      })
}


fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.size == size,
         { "Collection should have size $size but has size ${value.size}. Values: ${value.show().value}" },
         { "Collection should not have size $size. Values: ${value.show().value}" }
      )
}
