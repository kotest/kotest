package io.kotest.engine

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on JS")
