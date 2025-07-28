package io.kotest.assertions

actual fun createAssertionError(
   message: String,
   cause: Throwable?,
   expected: Expected?,
   actual: Actual?
): AssertionError {
   return KotestAssertionFailedError(
      message = message,
      cause = cause,
      expected = expected?.value?.value,
      actual = actual?.value?.value
   )
}
