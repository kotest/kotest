package io.kotest.engine

actual fun runPromise(f: suspend () -> Unit) {
   error("Promise is only available on kotest/js")
}
