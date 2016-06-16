package io.kotlintest

import java.util.concurrent.TimeUnit

interface Eventually {

  fun eventually(duration: Duration, f: () -> Unit): Unit {
    val end = System.nanoTime() + duration.nanoseconds
    var times = 0
    while (System.nanoTime() < end) {
      try {
        f()
        return
      } catch (e: Exception) {
        // ignore and proceed
      }
      times++
    }
    throw TestFailedException("Test failed after ${duration.amount} ${duration.timeUnit}; attempted $times times")
  }

  @Deprecated("use the overload with Duration instead", replaceWith = ReplaceWith("eventually(duration, f)"))
  fun eventually(duration: Long, unit: TimeUnit, f: () -> Unit): Unit {
    val end = System.nanoTime() + unit.toNanos(duration)
    var times = 0
    while (System.nanoTime() < end) {
      try {
        f()
        return
      } catch (e: Exception) {
      }
      times++
    }
    throw TestFailedException("Test failed after $duration $unit; attempted $times times")
  }
}