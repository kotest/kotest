package io.kotest.engine.spec

expect interface AutoCloseable {
   fun close()
}

/**
 * Closes an [AutoCloseable] when the spec is completed by registering an afterSpec listener
 * which invokes the [AutoCloseable.close] method.
 */
fun <T : AutoCloseable> TestSuite.autoClose(closeable: T): T {
   autoCloseables = listOf(closeable) + autoCloseables
   return closeable
}

/**
 * Closes a lazy [AutoCloseable] when the spec is completed by registering an afterSpec listener
 * which invokes the [AutoCloseable.close] method.
 */
fun <T : AutoCloseable> TestSuite.autoClose(closeable: Lazy<T>): Lazy<T> {
   autoClose(closeable.value)
   return closeable
}
