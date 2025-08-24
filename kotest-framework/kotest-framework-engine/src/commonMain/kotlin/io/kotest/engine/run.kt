package io.kotest.engine

expect fun <T> runBlocking(f: suspend () -> T): T

/**
 * Runs the given [f] function in a promise on JS or throws an exception on other platforms.
 *
 * @return a promise that resolves to the result of [f]. The return type must be defined as Any
 * because the Promise type is not available on all platforms.
 */
expect fun <T> runPromise(f: suspend () -> T): Any
