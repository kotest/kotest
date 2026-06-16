package io.kotest.matchers.string

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * A [io.kotest.matchers.Matcher] that runs the string preprocessors, and then calls the delegate matcher.
 */
class CharSequencePreprocessorMatcher(
   private val next: Matcher<CharSequence?>
) : Matcher<CharSequence?> {

   override fun test(value: CharSequence?): MatcherResult =
      next.test(if (value == null) null else StringPreprocessor.process(value.toString()))

   override fun invert(): Matcher<CharSequence?> =
      CharSequencePreprocessorMatcher(next.invert())
}
