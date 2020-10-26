package io.kotest.assertions

actual object Exceptions {
   actual fun createAssertionError(message: String, cause: Throwable?): AssertionError = AssertionError(message)
   actual fun createAssertionError(message: String, cause: Throwable?, expected: Expected, actual: Actual): Throwable =
      createAssertionError(message, cause)
}
