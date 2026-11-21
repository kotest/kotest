package io.kotest.engine.spec.execution

/**
 * Non-JVM implementation - fatal errors such as out-of-memory abort the process and
 * cannot be caught, so no throwable is considered a rethrowable fatal error here.
 */
internal actual fun Throwable.isFatalEngineError(): Boolean = false
