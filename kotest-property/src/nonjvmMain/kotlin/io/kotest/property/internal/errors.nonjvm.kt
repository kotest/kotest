package io.kotest.property.internal

import io.kotest.assertions.KotestAssertionFailedError

internal actual fun createPropertyAssertionError(message: String, cause: Throwable): AssertionError {
   // if the underlying failure carries expected/actual values, propagate them to the outer error
   // so IDEs and TeamCity service messages can render a diff for the outer error
   val kotestCause = cause as? KotestAssertionFailedError
   return if (kotestCause != null) {
      KotestAssertionFailedError(
         message = message,
         cause = cause,
         expected = kotestCause.expected,
         actual = kotestCause.actual,
      )
   } else {
      AssertionError(message, cause)
   }
}
