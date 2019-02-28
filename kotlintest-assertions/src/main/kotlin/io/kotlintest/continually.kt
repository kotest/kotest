package io.kotlintest

import java.time.Duration

fun <T> continually(duration: Duration, f: () -> T): T? {
  val start = System.nanoTime()
  val end = start + duration.toNanos()
  fun period() = Duration.ofNanos(System.nanoTime() - start)
  var times = 0
  var result: T? = null
  while (System.nanoTime() < end) {
    try {
      result = f()
    } catch (e: AssertionError) {
      if (times == 0)
        throw e
      throw Failures.failure("Test failed after ${period().toMillis()}ms; expected to pass for ${duration.toMillis()}ms; attempted $times times\nUnderlying failure was: ${e.message}", e)
    }
    times++
  }
  return result
}