package io.kotest.engine

actual inline fun <T> withDebugProbe(f: () -> T): T {
   return f()
}
