package io.kotest.comparators

import io.kotest.matchers.MatcherResult

class EqualityComparator<T> : Comparator<T> {
   override fun name(): String = "equality comparison"

   override fun matches(actual: T, expected: T): MatcherResult {
      return MatcherResult(
         passed = actual == expected,
         failureMessageFn = { "$actual should be equal to $expected" },
         negatedFailureMessageFn = { "$actual should not be equal to $expected" }
      )
   }
}

fun <T> Comparators.equality(): EqualityComparator<T> = EqualityComparator()
