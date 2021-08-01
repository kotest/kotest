package io.kotest.assertions

import io.kotest.common.errors.ComparisonError

actual object Exceptions {

   actual fun createAssertionError(
      message: String,
      cause: Throwable?
   ): AssertionError = AssertionError(message)

   actual fun createAssertionError(
      message: String,
      cause: Throwable?,
      expected: Expected,
      actual: Actual
   ): Throwable = AssertionFailedError(message, expected.value.value, actual.value.value)
}

class AssertionFailedError(
   message: String,
   override val expectedValue: String,
   override val actualValue: String
) : AssertionError(message), ComparisonError
