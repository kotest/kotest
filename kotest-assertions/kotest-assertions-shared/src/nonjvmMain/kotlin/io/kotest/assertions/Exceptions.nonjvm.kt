package io.kotest.assertions

actual val exceptions: Exceptions = BasicExceptions

object BasicExceptions : Exceptions {

   override fun createAssertionError(
      message: String,
      cause: Throwable?
   ): AssertionError = AssertionError(message)

   override fun createAssertionError(
      message: String,
      cause: Throwable?,
      expected: Expected,
      actual: Actual
   ): Throwable = AssertionFailedError(message, expected.value.value, actual.value.value)
}
