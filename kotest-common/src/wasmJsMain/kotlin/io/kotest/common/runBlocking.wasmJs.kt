package io.kotest.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on wasm/js")

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch { jsException ->
      println("$jsException")
      throw Throwable("$jsException")
   }
}
