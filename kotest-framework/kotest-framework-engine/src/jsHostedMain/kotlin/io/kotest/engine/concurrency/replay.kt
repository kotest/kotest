package io.kotest.engine.concurrency

actual suspend fun replay(
   times: Int,
   action: suspend (Int) -> Unit
) {
   repeat(times) {
      action(it)
   }
}
