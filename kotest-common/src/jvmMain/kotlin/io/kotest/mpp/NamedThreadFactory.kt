package io.kotest.mpp

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

private val counter = AtomicInteger(0)

class NamedThreadFactory(val name: String) : ThreadFactory {
   override fun newThread(r: Runnable): Thread {
      val t = Thread(r, String.format(name, counter.getAndIncrement()))
      t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
         e.printStackTrace()
      }
      return t
   }
}
