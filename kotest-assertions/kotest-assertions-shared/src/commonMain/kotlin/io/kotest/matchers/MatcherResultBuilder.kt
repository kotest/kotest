package io.kotest.matchers

import io.kotest.assertions.print.Printed

/**
 * A [MatcherResultBuilder] will build [MatcherResult]s, correctly returning
 * the appropriate concrete types to generate [click to see diff] links in the IDE output window
 * if expected and actual values are provided.
 *
 */
data class MatcherResultBuilder(
   private val passed: Boolean,
   private val expected: (() -> Printed)?,
   private val actual: (() -> Printed)?,
   private val failureMessageFn: () -> String,
   private val negatedFailureMessageFn: () -> String,
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

   fun build(): MatcherResult {
      return if (actual == null || expected == null)
         SimpleMatcherResult(passed, failureMessageFn, negatedFailureMessageFn)
      else
         DiffableMatcherResult(passed, expected, actual, failureMessageFn, negatedFailureMessageFn)
   }
}
