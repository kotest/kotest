package io.kotest.engine.concurrency

internal actual inline fun <T> withDebugProbe(f: () -> T): T {
   return f()
}
