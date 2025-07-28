package io.kotest.assertions

import org.opentest4j.AssertionFailedError

/**
 * Creates an [AssertionError] from the given message and expected and actual values
 * using the opentest4j library.
 *
 * The exception type is
 * See https://ota4j-team.github.io/opentest4j/docs/1.3.0/api/org/opentest4j/AssertionFailedError.html
 *
 */
actual fun createAssertionError(
   message: String,
   cause: Throwable?,
   expected: Expected?,
   actual: Actual?
): AssertionError {
   return try {
      // in the case of a mock, trying to access the cause's stacktrace in the assertion error constructor
      // will cause another exception to throw
      // easist workaround is to try to access the stack trace ourselves and catch any throwable
      cause?.stackTrace
      if (cause == null)
         AssertionFailedError(message, expected?.value?.value, actual?.value?.value)
      else
         AssertionFailedError(message, expected?.value?.value, actual?.value?.value, cause)
   } catch (_: Throwable) {
      AssertionFailedError(message, expected?.value?.value, actual?.value?.value)
   }
}
