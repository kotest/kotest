package io.kotest.assertions.eq

import io.kotest.assertions.failure
import io.kotest.assertions.show.show

object ThrowableEq : Eq<Throwable> {
   override fun equals(actual: Throwable, expected: Throwable): Throwable? {
      return if (actual.message == expected.message && expected::class == actual::class)
         null
      else
         failure(expected.show(), actual.show())
   }
}
