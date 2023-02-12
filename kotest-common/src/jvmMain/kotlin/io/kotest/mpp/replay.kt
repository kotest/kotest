package io.kotest.mpp

import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
      val executor = Executors.newFixedThreadPool(threads, NamedThreadFactory("replay-%d"))
      val error = AtomicReference<Throwable>(null)
      for (k in 0 until times) {
         executor.submit {
            runBlocking {
               try {
                  action(k)
               } catch (t: Throwable) {
                  error.compareAndSet(null, t)
               }
            }
         }
      }
      executor.shutdown()
      executor.awaitTermination(1, TimeUnit.DAYS)

      if (error.get() != null)
         throw error.get()
   }
}
