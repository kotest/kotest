package io.kotlintest

import java.time.Duration

fun <T> eventually(duration: Duration, f: () -> T): T = eventually(duration, Exception::class.java, f)

fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: Class<E>, f: () -> T): T {
  val end = System.nanoTime() + duration.toNanos()
  var times = 0
  var lastError: Throwable? = null
  while (System.nanoTime() < end) {
    try {
      return f()
    } catch (e: Throwable) {
      if (!exceptionClass.isAssignableFrom(e.javaClass) && !AssertionError::class.java.isAssignableFrom(e.javaClass)) {
        // Not the kind of exception we were prepared to tolerate
        throw e
      }
      lastError = e
      // else ignore and continue
    }
    times++
  }
  val underlyingCause = if (lastError == null) "" else "; underlying cause was ${lastError.localizedMessage}"
  throw Failures.failure("Test failed after ${duration.seconds} seconds; attempted $times times$underlyingCause", lastError)
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
  throw Failures.failure("Test failed after ${duration.seconds} seconds; attempted $times times")
}
