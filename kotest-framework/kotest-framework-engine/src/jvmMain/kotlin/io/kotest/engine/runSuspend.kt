package io.kotest.engine

import kotlinx.coroutines.runBlocking

actual fun runSuspend(f: suspend () -> Unit) {
   runBlocking { f() }
}
