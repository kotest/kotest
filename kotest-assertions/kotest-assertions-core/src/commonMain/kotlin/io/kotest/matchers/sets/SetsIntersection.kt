package io.kotest.matchers.sets

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Set<T>.shouldIntersect(ts: Set<T>) = this should intersectWith(ts)
infix fun <T> Set<T>.shouldNotIntersect(ts: Set<T>) = this shouldNot intersectWith(ts)

fun <T> intersectWith(ts: Set<T>): Matcher<Set<T>> = object : Matcher<Set<T>> {
   override fun test(value: Set<T>): MatcherResult {
      val intersection = ts.filter { value.contains(it) }
      val passed = intersection.isNotEmpty()

      val failure = { "Set should intersect with ${ts.print().value} but did not." }
      val negFailure = { "Set should not intersect with ${ts.print().value},\nbut had the following common element(s): ${intersection.print().value}" }

      return MatcherResult(passed, failure, negFailure)
   }
}
