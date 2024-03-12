package io.kotest.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch { jsException ->
      console.log("$jsException")
      throw Throwable("$jsException")
   }
}

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromiseIgnoringErrors(f: suspend () -> Unit) {
   GlobalScope.promise { f() }
}
