package io.kotest.matchers.iterator

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterator<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Iterator<T>.shouldNotHaveNext() = this.shouldBeEmpty()

fun <T> Iterator<T>.shouldHaveNext() = this.shouldNotBeEmpty()
fun <T> Iterator<T>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun <T> beEmpty(): Matcher<Iterator<T>> = object : Matcher<Iterator<T>> {
   override fun test(value: Iterator<T>): MatcherResult {
      return MatcherResult.invoke(
         !value.hasNext(),
         { "Iterator should be empty but still has values" },
         { "Iterator should not be empty" }
      )
   }
}
