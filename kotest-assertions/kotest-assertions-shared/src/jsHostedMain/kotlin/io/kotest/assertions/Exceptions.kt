package io.kotest.assertions

import io.kotest.common.errors.ComparisonError

actual object Exceptions {

   actual fun createAssertionError(
      message: String,
      cause: Throwable?
   ): AssertionError = AssertionError(message, cause)

   actual fun createAssertionError(
      message: String,
      cause: Throwable?,
      expected: Expected,
      actual: Actual
   ): Throwable =
      AssertionFailedError(message, cause, expected.value.value, actual.value.value)
}

class AssertionFailedError(
   message: String,
   cause: Throwable?,
   override val expectedValue: String,
   override val actualValue: String
) : AssertionError(message, cause), ComparisonError
