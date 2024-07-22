package io.kotest.engine

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch {
      io.kotest.core.console.log(it)
      throw it
   }
}
