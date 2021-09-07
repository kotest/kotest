package io.kotest.common

expect fun <T> runBlocking(f: suspend () -> T): T

expect fun runPromise(f: suspend () -> Unit)
