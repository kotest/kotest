package io.kotest.matchers

import io.kotest.assertions.print.Printed

/**
 * A [MatcherResultBuilder] will build [MatcherResult]s, correctly returning
 * the appropriate concrete types to generate [click to see diff] links in the IDE output window
 * if expected and actual values are provided.
 *
 * This class should be preferred over using [MatcherResult] directly.
 */
data class MatcherResultBuilder(
   private val passed: Boolean,
   private val expected: (() -> Printed)?,
   private val actual: (() -> Printed)?,
   private val failureMessageFn: () -> String,
   private val negatedFailureMessageFn: () -> String,
   private val error: Throwable? = null,
) {

   companion object {
      fun create(passed: Boolean): MatcherResultBuilder {
         return MatcherResultBuilder(
            passed,
            null,
            null,
            { "Matcher failed" },
            { "Matcher passed but failure was expected" }
         )
      }
   }

   fun withFailureMessage(failureMessageFn: () -> String): MatcherResultBuilder {
      return copy(failureMessageFn = failureMessageFn)
   }

   fun withNegatedFailureMessage(negatedFailureMessageFn: () -> String): MatcherResultBuilder {
      return copy(negatedFailureMessageFn = negatedFailureMessageFn)
   }

   fun withValues(expected: () -> Printed, actual: () -> Printed): MatcherResultBuilder {
      return copy(expected = expected, actual = actual)
   }

   fun withValues(expected: Printed, actual: Printed): MatcherResultBuilder {
      return copy(expected = { expected }, actual = { actual })
   }

   /**
    * Attaches a [Throwable] to this result. When [build] is called, the result will be a
    * [ThrowableMatcherResult], which causes [io.kotest.matchers.invokeMatcher] to rethrow
    * this error directly rather than constructing a new assertion error via
    * [io.kotest.assertions.AssertionErrorBuilder].
    */
   fun withError(error: Throwable): MatcherResultBuilder {
      return copy(error = error)
   }

   fun build(): MatcherResult {
      if (error != null) {
         return ThrowableMatcherResult(
            passed = passed,
            error = error,
            failureMessageFn = failureMessageFn,
            negatedFailureMessageFn = negatedFailureMessageFn
         )
      }
      return if (actual == null || expected == null)
         SimpleMatcherResult(
            passed = passed,
            failureMessageFn = failureMessageFn,
            negatedFailureMessageFn = negatedFailureMessageFn
         )
      else
         DiffableMatcherResult(
            passed = passed,
            actual = actual,
            expected = expected,
            failureMessageFn = failureMessageFn,
            negatedFailureMessageFn = negatedFailureMessageFn
         )
   }
}
