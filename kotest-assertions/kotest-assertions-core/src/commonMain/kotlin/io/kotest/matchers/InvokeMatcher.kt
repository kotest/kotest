package io.kotest.matchers

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.collectOrThrow

fun <T> invokeMatcher(t: T, matcher: Matcher<T>): T {
   assertionCounter.inc()
   val result = matcher.test(t)
   if (!result.passed()) {

      val error = when (result) {

         is DiffableMatcherResult -> AssertionErrorBuilder.create()
            .withMessage(result.failureMessage() + "\n")
            .withValues(
               expected = Expected(result.expected()),
               actual = Actual(result.actual())
            ).build()


         is MatcherResultWithError -> result.error() ?: AssertionErrorBuilder.create()
            .withMessage(result.failureMessage()).build()

         else -> AssertionErrorBuilder.create().withMessage(result.failureMessage()).build()
      }

      errorCollector.collectOrThrow(error)
   }

   return t
}

internal class MatcherResultWithError(
   val passed: Boolean,
   val error: () -> Throwable?,
   val failureMessageFn: (error: Throwable?) -> String,
   val negatedFailureMessageFn: (error: Throwable?) -> String,
) : MatcherResult {
   override fun passed(): Boolean = passed
   override fun failureMessage(): String = failureMessageFn(error())
   override fun negatedFailureMessage(): String = negatedFailureMessageFn(error())
}
