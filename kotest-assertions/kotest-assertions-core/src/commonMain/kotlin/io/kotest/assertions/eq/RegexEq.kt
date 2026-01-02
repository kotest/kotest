package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

/**
 * An implementation of [Eq] for comparing [Regex]s.
 */
internal object RegexEq : Eq<Regex> {

   override fun equals(actual: Regex, expected: Regex, context: EqContext): EqResult {
      return patternsAreNotEqual(actual, expected)
         .flatMapIfEqual { optionsAreNotEqual(actual, expected) }
   }

   private fun patternsAreNotEqual(actual: Regex, expected: Regex): EqResult {
      return if (actual.pattern == expected.pattern) EqResult.Success else EqResult.failure {
         AssertionErrorBuilder.create()
            .withValues(
               Expected(Printed(expected.pattern)),
               Actual(Printed(actual.pattern))
            ).build()
      }
   }

   private fun optionsAreNotEqual(actual: Regex, expected: Regex): EqResult {
      return if (actual.options == expected.options) EqResult.Success else EqResult.failure {
         AssertionErrorBuilder.create()
            .withValues(
               Expected(expected.options.print()),
               Actual(actual.options.print())
            ).build()
      }
   }
}

