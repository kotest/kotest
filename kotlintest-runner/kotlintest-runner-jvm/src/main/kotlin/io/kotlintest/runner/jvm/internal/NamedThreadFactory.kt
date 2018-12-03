package io.kotlintest.runner.jvm.internal

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NamedThreadFactory(val name: String) : ThreadFactory {
  private val counter = AtomicInteger(0)
  override fun newThread(r: Runnable): Thread {
    return Thread(r, String.format(name, counter.getAndIncrement()))
  }
}