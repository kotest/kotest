package io.kotest.until

import io.kotest.assertions.Failures
import java.time.Duration
import java.time.temporal.ChronoUnit

interface UntilListener<in T> {
  fun onEval(t: T)

  companion object {
    val noop = object : UntilListener<Any?> {
      override fun onEval(t: Any?) {}
    }
  }
}

fun <T> untilListener(f: (T) -> Unit) = object : UntilListener<T> {
  override fun onEval(t: T) {
    f(t)
  }
}

fun until(duration: Duration,
          interval: Interval = fixedInterval(Duration.ofSeconds(1)),
          f: () -> Boolean) {
  val end = System.nanoTime() + duration.toNanos()
  var count = 0
  while (System.nanoTime() < end) {
    val result = f()
    if (result)
      return
    count++
    Thread.sleep(interval.next(count).toMillis())
  }
  throw Failures.failure("Test failed after ${duration.seconds} seconds; attempted $count times")
}

fun <T> until(duration: Duration,
              predicate: (T) -> Boolean,
              f: () -> T): T = until(
    duration,
    fixedInterval(Duration.of(1, ChronoUnit.SECONDS)),
    predicate = predicate,
    listener = UntilListener.noop,
    f = f
)

fun <T> until(duration: Duration,
              interval: Interval,
              predicate: (T) -> Boolean,
              f: () -> T): T = until(
    duration,
    interval,
    predicate = predicate,
    listener = UntilListener.noop,
    f = f
)

fun <T> until(duration: Duration,
              interval: Interval,
              predicate: (T) -> Boolean,
              listener: UntilListener<T>,
              f: () -> T): T {
  val end = System.nanoTime() + duration.toNanos()
  var count = 0
  while (System.nanoTime() < end) {
    val result = f()
    if (predicate(result)) {
      return result
    } else {
      listener.onEval(result)
      count++
    }
    Thread.sleep(interval.next(count).toMillis())
  }
  throw Failures.failure("Test failed after ${duration.seconds} seconds; attempted $count times")
}
