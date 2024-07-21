package io.kotest.engine

expect fun <T> runBlocking(f: suspend () -> T): T

expect fun runPromise(f: suspend () -> Unit)
