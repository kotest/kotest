package io.kotest.assertions

actual fun <T : Throwable> cleanStackTrace(throwable: T): T = throwable

/**
 * Creates an [AssertionError] from the given message. If the platform supports nested exceptions, the cause
 * is set to the given [cause]. If the platform supports stack traces, then the stack is cleaned of `io.kotest`
 * lines.
 */
actual fun createAssertionError(message: String, cause: Throwable?): AssertionError {
   return AssertionError(message)
}

/**
 * Creates an [AssertionError] from the given message and expected and actual values. If the platform
 * supports nested exceptions, the cause is set to the given [cause]. If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 *
 * If the platform has jUnit4 or jUnit5 on the classpath, it will use exceptions from those platforms.
 */
actual fun createAssertionError(
   message: String,
   cause: Throwable?,
   expected: Expected, actual: Actual
): Throwable = createAssertionError(message, cause)
