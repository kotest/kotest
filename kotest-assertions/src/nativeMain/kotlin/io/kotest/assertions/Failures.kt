package io.kotest.assertions

actual object Failures {
  actual fun failure(message: String): AssertionError = failure(message, null)
  actual fun failure(message: String, cause: Throwable?): AssertionError = AssertionError(message)
  actual fun clean(throwable: Throwable): Throwable = throwable
  actual fun failure(message: String, expectedRepr: String, actualRepr: String): Throwable = failure(message)
}
