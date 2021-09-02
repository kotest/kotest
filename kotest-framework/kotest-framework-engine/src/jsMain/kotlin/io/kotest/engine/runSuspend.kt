package io.kotest.engine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun runSuspend(f: suspend () -> Unit) {
   GlobalScope.promise {
      f()
   }
}
