package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

object ThrowableEq : Eq<Throwable> {
   override fun equals(actual: Throwable, expected: Throwable, strictNumberEq: Boolean) =
      EqResult(actual.message == expected.message && expected::class == actual::class) {
         failure(Expected(expected.print()), Actual(actual.print()))
      }
}
