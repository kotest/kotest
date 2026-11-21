package io.kotest.engine.spec.execution

/**
 * Returns true if this throwable is a fatal error that must be rethrown rather than
 * swallowed by the engine, even when it originates from an internal listener.
 *
 * On the JVM this covers OutOfMemoryError and StackOverflowError; on other platforms
 * fatal conditions (such as out-of-memory) abort the process and cannot be caught, so
 * no throwable is considered fatal there.
 */
internal expect fun Throwable.isFatalEngineError(): Boolean
