package io.kotest.engine

internal actual inline fun <T> withDebugProbe(f: () -> T): T {
   return f()
}
