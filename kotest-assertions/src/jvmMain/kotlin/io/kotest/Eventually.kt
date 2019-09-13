package io.kotest

import io.kotest.assertions.Failures
import io.kotest.until.UntilListener
import io.kotest.until.fixedInterval
import io.kotest.until.until
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
  throw Failures.failure("Test failed after ${duration.seconds} seconds; attempted $times times$underlyingCause",
      lastError)
}

@Deprecated("To use eventually with a predicate, use await()", ReplaceWith("await(duration, interval, predicate, f)"))
fun <T> eventually(duration: Duration, predicate: (T) -> Boolean, f: () -> T): T =
    until(
        duration = duration,
        interval = fixedInterval(Duration.ofMillis(500)),
        listener = UntilListener.noop,
        predicate = predicate,
        f = f
    )
