package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

/**
 * An [Eq] for comparing a [Throwable] to another [Throwable].
 */
object ThrowableEq : Eq<Throwable> {
   override fun equals(actual: Throwable, expected: Throwable, strictNumberEq: Boolean): Throwable? {
      return if (actual.message == expected.message && expected::class == actual::class)
         null
      else
         failure(Expected(expected.print()), Actual(actual.print()))
   }
}
