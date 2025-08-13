package io.kotest.engine

actual fun <T> runPromise(f: suspend () -> T): Any {
   error("runPromise is not available on wasmWasi")
}

actual fun <T> runBlocking(f: suspend () -> T): T {
   error("runBlocking is not available on wasmWasi")
}
