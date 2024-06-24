package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.invokeMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T, I : Iterable<T>> I?.shouldBeEmpty(): I {
   if (this == null) fail()
   toList().shouldBeEmpty()
   return this
}

fun <T> Array<T>?.shouldBeEmpty(): Array<T> {
   if (this == null) fail()
   this should beEmptyArray()
   return this
}

fun <T, C : Collection<T>> C?.shouldBeEmpty(): C {
   if (this == null) fail()
   this should beEmpty()
   return this
}

fun <T, I : Iterable<T>> I?.shouldNotBeEmpty(): I {
   if (this == null) fail()
   toList().shouldNotBeEmpty()
   return this
}

fun <T> Array<T>?.shouldNotBeEmpty(): Array<T> {
   if (this == null) fail()
   asList().shouldNotBeEmpty()
   return this
}

fun <T, C : Collection<T>> C?.shouldNotBeEmpty(): C {
   if (this == null) fail()
   this shouldNot beEmpty()
   return this
}

fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "Collection should be empty but contained ${value.first().print().value}" },
      { "Collection should not be empty" }
   )
}

fun <T> beEmptyArray(): Matcher<Array<T>> = object : Matcher<Array<T>> {
   override fun test(value: Array<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "Array should be empty but contained ${value.first().print().value}" },
      { "Array should not be empty" }
   )
}

private fun fail(): Nothing {
   invokeMatcher(null, Matcher.failure("Expected an array collection but was null"))
   throw NotImplementedError()
}
