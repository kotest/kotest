package io.kotest.assertions.eq

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultWithError

/**
 * A matcher that checks if a value is equal to another value using an
 * instance of the typeclass [Eq].
 */
class EqMatcher<T>(private val expected: T) : Matcher<T> {

   override fun test(value: T): MatcherResult {
      val error = eq(value, expected)

      return MatcherResultWithError(
         error = error,
         passed = error == null,
         failureMessageFn = { e ->
            e?.message ?: "${expected.print().value} should be equal to ${value.print().value}"
         },
         negatedFailureMessageFn = { e ->
            e?.message ?: "${expected.print().value} should not equal ${value.print().value}"
         }
      )
   }
}
