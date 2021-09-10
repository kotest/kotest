package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterable<T>.shouldBeUnique(): Iterable<T> {
   toList().shouldBeUnique()
   return this
}

fun <T> Array<T>.shouldBeUnique(): Array<T> {
   asList().shouldBeUnique()
   return this
}

fun <T> Collection<T>.shouldBeUnique(): Collection<T> {
   this should beUnique()
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
   override fun test(value: Collection<T>) = MatcherResult(
      value.toSet().size == value.size,
      { "Collection should be Unique" },
      {
         "Collection should contain at least one duplicate element"
      })
}
