package io.kotest.engine

actual fun <T> runPromise(f: suspend () -> T): Any {
   error("runBlocking is not available on jvm")
}
