package io.kotest.engine.concurrency

actual inline fun <T> withDebugProbe(f: () -> T): T {
   return f() // jvm only feature so just invoke the function directly
}
