package io.kotest.runner.jvm.internal

import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(val name: String) : ThreadFactory {
   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val counter = AtomicInteger(0)
   override fun newThread(r: Runnable): Thread {
      val t = Thread(r, String.format(name, counter.getAndIncrement()))
      t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
         logger.error("Error in executor", e)
      }
      return t
   }
}
