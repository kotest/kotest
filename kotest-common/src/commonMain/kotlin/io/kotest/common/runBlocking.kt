package io.kotest.common

expect fun <T> runBlocking(f: suspend () -> T): T

expect fun runPromiseCatching(f: suspend () -> Unit)

expect fun runPromise(f: suspend () -> Unit)
