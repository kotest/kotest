package io.kotest.property.internal

import org.opentest4j.AssertionFailedError

internal actual fun createPropertyAssertionError(message: String, cause: Throwable): AssertionError {
   // if the underlying failure is an OpenTest4J AssertionFailedError carrying expected/actual values,
   // propagate them to the outer error so IntelliJ can render a "click to see diff" link
   val opentestCause = cause as? AssertionFailedError
   val expected = opentestCause?.takeIf { it.isExpectedDefined }?.expected?.value
   val actual = opentestCause?.takeIf { it.isActualDefined }?.actual?.value
   return if (expected != null || actual != null) {
      AssertionFailedError(message, expected, actual, cause)
   } else {
      AssertionFailedError(message, cause)
   }
}
