package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print

/**
 * An implementation of [Eq] for comparing [Regex]s.
 */
internal object RegexEq : Eq<Regex> {
   override fun equals(actual: Regex, expected: Regex, strictNumberEq: Boolean): EqResult {
      return and(patternsAreEqual(actual, expected), optionsAreEqual(actual, expected))
   }
}

private fun patternsAreEqual(actual: Regex, expected: Regex) =
   EqResult(actual.pattern == expected.pattern) {
      failure(
         Expected(Printed(expected.pattern)),
         Actual(Printed(actual.pattern))
      )
   }

private fun optionsAreEqual(actual: Regex, expected: Regex) =
   EqResult(actual.options == expected.options) {
      failure(
         Expected(expected.options.print()),
         Actual(actual.options.print())
      )
   }
