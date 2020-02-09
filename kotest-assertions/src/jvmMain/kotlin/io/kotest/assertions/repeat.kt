package io.kotest.assertions

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

actual fun replay(
   times: Int,
   threads: Int,
   before: (Int) -> Unit,
   after: (Int) -> Unit,
   action: (Int) -> Unit
) {
   if (threads == 1) {
      simpleRepeat(times, before, after, action)
   } else {
      val executor = Executors.newFixedThreadPool(threads)
      val error = AtomicReference<Throwable>(null)
      for (k in 0 until times) {
         executor.submit {
            try {
               before(k)
               action(k)
            } catch (t: Throwable) {
               error.compareAndSet(null, t)
            } finally {
               after(k)
            }
         }
      }
      executor.shutdown()
      executor.awaitTermination(1, TimeUnit.DAYS)

      if (error.get() != null)
         throw error.get()
   }
}
