package io.kotest.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch {
      println(it)
      throw it
   }
}

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on JS")
