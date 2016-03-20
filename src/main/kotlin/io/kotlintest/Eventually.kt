package io.kotlintest

import java.util.concurrent.TimeUnit

interface Eventually {

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