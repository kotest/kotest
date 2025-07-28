package io.kotest.matchers

import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

fun <T> invokeMatcher(t: T, matcher: Matcher<T>): T {
   assertionCounter.inc()
   val result = matcher.test(t)
   if (!result.passed()) {
      when (result) {

         is ValuesMatcherResult -> errorCollector.collectOrThrow(
            AssertionErrorBuilder.create()
               .withMessage(result.failureMessage() + "\n")
               .withValues(
                  expected = Expected(result.expected),
                  actual = Actual(result.actual)
               ).build()
         )

         is ComparableMatcherResult -> errorCollector.collectOrThrow(
            AssertionErrorBuilder.create()
               .withMessage(result.failureMessage() + "\n")
               .withValues(
                  expected = Expected(Printed(result.expected())),
                  actual = Actual(Printed(result.actual()))
               ).build()
         )

         is EqualityMatcherResult -> errorCollector.collectOrThrow(
            AssertionErrorBuilder.create()
               .withMessage(result.failureMessage() + "\n")
               .withValues(
                  expected = Expected(result.expected().print()),
                  actual = Actual(result.actual().print())
               ).build()
         )

         is MatcherResultWithError -> {
            val error = result.error ?: AssertionErrorBuilder.create().withMessage(result.failureMessage()).build()
            errorCollector.collectOrThrow(error)
         }

         else -> errorCollector.collectOrThrow(
            AssertionErrorBuilder.create().withMessage(result.failureMessage()).build()
         )
      }
   }
   return t
}
