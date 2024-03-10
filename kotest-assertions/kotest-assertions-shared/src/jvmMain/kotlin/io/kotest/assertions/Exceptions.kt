package io.kotest.assertions

import io.kotest.common.errors.ComparisonError

actual object Exceptions {

   /**
    * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
    * is set to the given [cause].
    */
   actual fun createAssertionError(message: String, cause: Throwable?): AssertionError = try {
      // in the case of a mock, trying to access the cause's stacktrace in the assertion error constructor
      // will cause another exception to throw
      // easist workaround is to try to access the stack trace ourselves and catch any throwable
      cause?.stackTrace
      AssertionError(message, cause)
   } catch (e: Throwable) {
      AssertionError(message, null)
   }

   /**
    * Creates an [AssertionError] from the given message and expected and actual values.
    *
    * The exception type is
    *
    *
    * See https://ota4j-team.github.io/opentest4j/docs/1.0.0/api/org/opentest4j/AssertionFailedError.html
    *
    */
   actual fun createAssertionError(
      message: String,
      cause: Throwable?,
      expected: Expected,
      actual: Actual
   ): Throwable = try {
      // in the case of a mock, trying to access the cause's stacktrace in the assertion error constructor
      // will cause another exception to throw
      // easist workaround is to try to access the stack trace ourselves and catch any throwable
      cause?.stackTrace
      AssertionFailedError(message, cause, expected.value.value, actual.value.value)
   } catch (e: Throwable) {
      AssertionFailedError(message, null, expected.value.value, actual.value.value)
   }
}

/**
 * This is our extension of the opentest4j error type which adds the interface [ComparisonError] which
 * is the Kotest multiplatform interface for errors that expose expected and actual values.
 */
class AssertionFailedError(
   message: String,
   cause: Throwable?,
   override val expectedValue: String,
   override val actualValue: String,
) : org.opentest4j.AssertionFailedError(message, expectedValue, actualValue, cause), ComparisonError
