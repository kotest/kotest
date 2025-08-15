package io.kotest.engine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun <T> runPromise(f: suspend () -> T): Any {
   return GlobalScope.promise { f() }.catch {
      println(it)
      throw it
   }
}

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on JS")
