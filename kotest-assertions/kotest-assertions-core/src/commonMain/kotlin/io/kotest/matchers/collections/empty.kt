package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterable<T>.shouldBeEmpty(): Iterable<T> {
   toList().shouldBeEmpty()
   return this
}

fun <T> Array<T>.shouldBeEmpty(): Array<T> {
   asList().shouldBeEmpty()
   return this
}

fun <T> Collection<T>.shouldBeEmpty(): Collection<T> {
   this should beEmpty()
   return this
}

fun <T> Iterable<T>.shouldNotBeEmpty(): Iterable<T> {
   toList().shouldNotBeEmpty()
   return this
}

fun <T> Array<T>.shouldNotBeEmpty(): Array<T> {
   asList().shouldNotBeEmpty()
   return this
}

fun <T> Collection<T>.shouldNotBeEmpty(): Collection<T> {
   this shouldNot beEmpty()
   return this
}

fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "Collection should be empty but contained ${value.show().value}" },
      { "Collection should not be empty" }
   )
}
