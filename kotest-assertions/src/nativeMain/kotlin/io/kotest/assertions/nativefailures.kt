package io.kotest.assertions

actual fun cleanStackTrace(throwable: Throwable): Throwable = throwable

/**
 * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
 * is set to the given [cause]. If the platform supports stack traces, then the stack is cleaned of `io.kotest`
 * lines.
 */
actual fun createAssertionError(message: String, cause: Throwable?): AssertionError {
   return AssertionError(message, cause)
}
