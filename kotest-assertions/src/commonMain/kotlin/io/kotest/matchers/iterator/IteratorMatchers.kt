package io.kotest.matchers.iterator

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.assertions.stringRepr
import io.kotest.should
import io.kotest.shouldNot

fun <T> Iterator<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Iterator<T>.shouldNotBeEmpty() = this shouldNot beEmpty()
fun <T> beEmpty(): Matcher<Iterator<T>> = object : Matcher<Iterator<T>> {
   override fun test(value: Iterator<T>): MatcherResult {
      return MatcherResult(
         !value.hasNext(),
         "Iterator should be empty but still has values",
         "Iterator should not be empty"
      )
   }
}
