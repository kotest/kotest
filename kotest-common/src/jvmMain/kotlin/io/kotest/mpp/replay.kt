package io.kotest.mpp

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

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
      val error = AtomicReference<Throwable>(null)

      @OptIn(DelicateCoroutinesApi::class)
      newFixedThreadPoolContext(threads, "replay").use { dispatcher ->
         withContext(dispatcher) {
            for (k in 0 until times) {
               launch {
                  try {
                     action(k)
                  } catch (t: Throwable) {
                     error.compareAndSet(null, t)
                  }
               }
            }
         }
      }

      if (error.get() != null)
         throw error.get()
   }
}
