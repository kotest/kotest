package io.kotlintest.await

import io.kotlintest.Failures
import io.kotlintest.eventually.PollInterval
import io.kotlintest.eventually.fixedInterval
import java.time.Duration
import java.time.temporal.ChronoUnit

interface AwaitListener<in T> {
  fun onEval(t: T)

  companion object {
    val noop = object : AwaitListener<Any?> {
      override fun onEval(t: Any?) {}
    }
  }
}

fun <T> awaitListener(f: (T) -> Unit) = object : AwaitListener<T> {
  override fun onEval(t: T) {
    f(t)
  }
}

fun await(duration: Duration,
          interval: PollInterval = fixedInterval(Duration.of(1, ChronoUnit.SECONDS)),
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

fun <T> await(duration: Duration,
              predicate: (T) -> Boolean,
              f: () -> T): T = await(
    duration,
    fixedInterval(Duration.of(1, ChronoUnit.SECONDS)),
    predicate = predicate,
    listener = AwaitListener.noop,
    f = f
)

fun <T> await(duration: Duration,
              interval: PollInterval,
              predicate: (T) -> Boolean,
              f: () -> T): T = await(
    duration,
    interval,
    predicate = predicate,
    listener = AwaitListener.noop,
    f = f
)

fun <T> await(duration: Duration,
              interval: PollInterval,
              predicate: (T) -> Boolean,
              listener: AwaitListener<T>,
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