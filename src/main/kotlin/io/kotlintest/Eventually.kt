package io.kotlintest

fun <T>eventually(duration: Duration, f: () -> T): T {
  val end = System.nanoTime() + duration.nanoseconds
  var times = 0
  while (System.nanoTime() < end) {
    try {
      return f()
    } catch (e: Exception) {
      // ignore and proceed
    }
    times++
  }
  throw AssertionError("Test failed after ${duration.amount} ${duration.timeUnit}; attempted $times times")
}