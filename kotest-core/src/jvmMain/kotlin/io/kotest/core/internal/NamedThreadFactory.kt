package io.kotest.core.internal

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(val name: String) : ThreadFactory {
   private val counter = AtomicInteger(0)
   override fun newThread(r: Runnable): Thread {
      val t = Thread(r, String.format(name, counter.getAndIncrement()))
      t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
         println("Error in executor")
         e.printStackTrace()
      }
      return t
   }
}
