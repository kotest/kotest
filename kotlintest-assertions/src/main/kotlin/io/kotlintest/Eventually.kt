package io.kotlintest

import java.lang.Exception
import java.time.Duration

fun <T> eventually(duration: Duration, f: () -> T): T = eventually(duration, Exception::class.java, f)

fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: Class<E>, f: () -> T): T {
  val end = System.nanoTime() + duration.toNanos()
  var times = 0
  while (System.nanoTime() < end) {
    try {
      return f()
    } catch (e: Throwable) {
      if (!exceptionClass.isAssignableFrom(e.javaClass)) {
        // Not the kind of exception we were prepared to tolerate
        throw e
      }
      // else ignore and continue
    }
    times++
  }
  throw AssertionError("Test failed after ${duration.seconds} seconds; attempted $times times")
}

fun <T> eventually(duration: Duration, predicate: (T) -> Boolean, f: () -> T): T {
  val end = System.nanoTime() + duration.toNanos()
  var times = 0
  while (System.nanoTime() < end) {
    val result = f()
    if (predicate(result)) {
      return result
    } else {
      times++
    }
  }
  throw AssertionError("Test failed after ${duration.seconds} seconds; attempted $times times")
}