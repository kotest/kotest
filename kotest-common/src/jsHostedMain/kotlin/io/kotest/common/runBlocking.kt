package io.kotest.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on JS")

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch {
      console.log(it)
      throw it as Throwable
   }
}
