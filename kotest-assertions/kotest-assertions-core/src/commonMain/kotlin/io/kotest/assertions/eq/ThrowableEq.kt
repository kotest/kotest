package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print

/**
 * An [Eq] for comparing a [Throwable] to another [Throwable].
 */
object ThrowableEq : Eq<Throwable> {
   @Deprecated("Use the overload with one more parameter of type EqContext.")
   override fun equals(actual: Throwable, expected: Throwable, strictNumberEq: Boolean): Throwable? =
      equals(actual, expected, strictNumberEq, EqContext())

   override fun equals(actual: Throwable, expected: Throwable, strictNumberEq: Boolean, context: EqContext): Throwable? {
      return if (actual.message == expected.message && expected::class == actual::class)
         null
      else
         AssertionErrorBuilder.create()
            .withValues(Expected(expected.print()), Actual(actual.print()))
            .build()
   }
}
