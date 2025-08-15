package io.kotest.engine

actual fun <T> runBlocking(f: suspend () -> T): T = kotlinx.coroutines.runBlocking { f() }

actual fun <T> runPromise(f: suspend () -> T): Any {
   error("runBlocking is not available on jvm")
}
