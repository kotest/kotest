package io.kotest.engine

actual fun <T> runBlocking(f: suspend () -> T): T = kotlinx.coroutines.runBlocking { f() }

actual fun runPromise(f: suspend () -> Unit) {
   error("Promise is only available on kotest/js")
}
