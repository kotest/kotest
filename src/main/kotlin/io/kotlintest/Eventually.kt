package io.kotlintest

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
  throw AssertionError("Test failed after ${duration.amount} ${duration.timeUnit}; attempted $times times")
}