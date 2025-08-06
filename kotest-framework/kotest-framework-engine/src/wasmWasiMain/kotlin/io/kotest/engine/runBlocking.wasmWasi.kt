package io.kotest.engine

actual fun runPromise(f: suspend () -> Unit) {
   error("runPromise is not available on wasmWasi")
}

actual fun <T> runBlocking(f: suspend () -> T): T {
   error("runBlocking is not available on wasmWasi")
}
