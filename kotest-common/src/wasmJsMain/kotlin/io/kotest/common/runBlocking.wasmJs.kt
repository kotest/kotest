package io.kotest.common

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromiseCatching(f: suspend () -> Unit) {
   GlobalScope.promise { f() }.catch {
      console.log(it)
      // FIXME: workaround for KT-64357 K/Wasm: "CompileError: WebAssembly.Module(): Invalid types for ref.test ..."
      //     When resolved, the following line is to be replaced by `throw it as Throwable`.
      throw Throwable("$it")
   }
}

@OptIn(DelicateCoroutinesApi::class)
actual fun runPromise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }
}
