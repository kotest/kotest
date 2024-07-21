package io.kotest.engine.concurrency

actual suspend fun replay(
   times: Int,
   threads: Int,
   action: suspend (Int) -> Unit,
) {
   require(threads == 1) { "Cannot run Native tests on multiple threads. Use the built in kotlin function repeat(n)" }
   repeat(times) {
      action(it)
   }
}
