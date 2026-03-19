package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResult.Companion.invoke
import io.kotest.matchers.neverNullMatcher

fun <T> existInOrder(vararg ps: (T) -> Boolean): Matcher<Collection<T>?> = existInOrder(ps.asList())

fun <T> existInOrder(predicates: List<(T) -> Boolean>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   require(predicates.isNotEmpty()) { "predicates must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < predicates.size) {
      if (predicates[subsequenceIndex](actualIterator.next())) subsequenceIndex += 1
   }

   val passed = subsequenceIndex == predicates.size

   val predicateMatchedOutOfOrderDescription = {
      val predicateMatchedOutOfOrderIndexes = if (passed) emptyList() else {
         actual.mapIndexedNotNull { index, element ->
            if (predicates[subsequenceIndex](element)) index else null
         }
      }
      if (predicateMatchedOutOfOrderIndexes.isEmpty()) "" else
         ",\nbut found element(s) matching the predicate out of order at index(es): ${predicateMatchedOutOfOrderIndexes.print().value}"
   }

   MatcherResult(
      passed,
      { "${actual.print().value} did not match the predicates in order. Predicate at index $subsequenceIndex did not match.${predicateMatchedOutOfOrderDescription()}" },
      { "${actual.print().value} should not match the predicates in order" }
   )
}
