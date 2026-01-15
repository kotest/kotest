package io.kotest.engine

actual fun <T> runPromise(f: suspend () -> T): Any {
   error("runPromise is not available on wasmWasi")
}
