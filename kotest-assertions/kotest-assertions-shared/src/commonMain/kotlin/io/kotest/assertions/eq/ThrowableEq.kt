package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object ThrowableEq : Eq<Throwable> {
   override fun equals(actual: Throwable, expected: Throwable): Throwable? {
      return if (actual.message == expected.message && expected::class == actual::class)
         null
      else
         failure(Expected(expected.show()), Actual(actual.show()))
   }
}
