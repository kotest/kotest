package io.kotest.matchers

import io.kotest.assertions.*
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

fun <T> invokeMatcher(t: T, matcher: Matcher<T>): T {
   assertionCounter.inc()
   val result = matcher.test(t)
   if (!result.passed()) {
      when (result) {
         is ComparableMatcherResult -> errorCollector.collectOrThrow(
           failure(
             expected = Expected(Printed(result.expected())),
             actual = Actual(Printed(result.actual())),
             prependMessage = result.failureMessage() + "\n"
           )
         )
         is EqualityMatcherResult -> errorCollector.collectOrThrow(
           failure(
             expected = Expected(result.expected().print()),
             actual = Actual(result.actual().print()),
             prependMessage = result.failureMessage() + "\n"
           )
         )
         is MatcherResultWithError -> {
            val error = result.error ?: failure(result.failureMessage())
            errorCollector.collectOrThrow(error)
         }
         else -> errorCollector.collectOrThrow(failure(result.failureMessage()))
      }
   }
   return t
}
