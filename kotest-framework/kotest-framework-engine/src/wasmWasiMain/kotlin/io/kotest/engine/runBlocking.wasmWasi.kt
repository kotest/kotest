package io.kotest.engine

actual fun runPromise(f: suspend () -> Unit) {
   error("Promise is only available on kotest/js")
}

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on wasmWasi")
