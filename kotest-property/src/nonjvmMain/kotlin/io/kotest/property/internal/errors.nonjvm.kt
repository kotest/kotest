package io.kotest.property.internal

import io.kotest.assertions.KotestAssertionFailedError

internal actual fun createPropertyAssertionError(message: String, cause: Throwable): AssertionError {
   // if the underlying failure carries expected/actual values, propagate them to the outer error
   // so IDEs and TeamCity service messages can render a diff for the outer error
   return when(cause) {
      is KotestAssertionFailedError -> KotestAssertionFailedError(
         message = message,
         cause = cause,
         expected = cause.expected,
         actual = cause.actual,
      )
      else -> AssertionError(message, cause)
   }
}
