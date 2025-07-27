package io.kotest.common

import kotlinx.coroutines.runBlocking

actual fun <T> runBlocking(f: suspend () -> T): T = runBlocking { f() }

actual fun runPromise(f: suspend () -> Unit) {
   error("Promise is only available on kotest/js")
}
