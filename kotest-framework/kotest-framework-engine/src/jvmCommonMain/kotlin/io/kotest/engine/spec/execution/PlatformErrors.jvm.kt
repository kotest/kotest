package io.kotest.engine.spec.execution

/**
 * JVM implementation - OutOfMemoryError and StackOverflowError corrupt the engine state
 * and must not be silently swallowed.
 */
internal actual fun Throwable.isFatalEngineError(): Boolean =
   this is OutOfMemoryError || this is StackOverflowError
