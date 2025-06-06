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

   override fun equals(actual: Regex, expected: Regex, strictNumberEq: Boolean): Throwable? {
      return patternsAreNotEqual(actual, expected) ?: optionsAreNotEqual(actual, expected)
   }

   private fun patternsAreNotEqual(actual: Regex, expected: Regex): Throwable? {
      return if (actual.pattern == expected.pattern) null else failure(
         Expected(Printed(expected.pattern)),
         Actual(Printed(actual.pattern))
      )
   }

   private fun optionsAreNotEqual(actual: Regex, expected: Regex): Throwable? {
      return if (actual.options == expected.options) null else failure(
         Expected(expected.options.print()),
         Actual(actual.options.print())
      )
   }

}

