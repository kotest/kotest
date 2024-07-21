package io.kotest.engine.concurrency

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

actual suspend fun replay(
   times: Int,
   threads: Int,
   action: suspend (Int) -> Unit
) {
   if (threads == 1) {
      repeat(times) {
         action(it)
      }
   } else {
      @OptIn(DelicateCoroutinesApi::class)
      newFixedThreadPoolContext(threads, "replay").use { dispatcher ->
         withContext(dispatcher) {
            repeat(times) {
               launch {
                  action(it)
               }
            }
         }
      }
   }
}
