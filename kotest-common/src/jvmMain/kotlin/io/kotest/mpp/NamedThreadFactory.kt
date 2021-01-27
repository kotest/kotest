package io.kotest.mpp

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

private val counter = AtomicInteger(0)

class NamedThreadFactory(val name: String, private val daemon: Boolean = false) : ThreadFactory {
   override fun newThread(r: Runnable): Thread {
      val t = Thread(r, String.format(name, counter.getAndIncrement()))
      t.isDaemon = daemon
      t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
         e.printStackTrace()
      }
      return t
   }
}
