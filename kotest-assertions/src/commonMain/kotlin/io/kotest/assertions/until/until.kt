package io.kotest.assertions.until

import io.kotest.assertions.failure
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.seconds

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

/**
 * Executes a function until it returns true or the duration elapses.
 *
 * @param f the function to execute
 * @param duration the maximum amount of time to continue trying for success
 * @param interval the delay between invocations
 */
@OptIn(ExperimentalTime::class)
suspend fun until(
   duration: Duration,
   interval: Interval = 1.seconds.fixed(),
   f: () -> Boolean
) = until(duration, interval, { it }, UntilListener.noop, f)

@OptIn(ExperimentalTime::class)
suspend fun <T> until(
   duration: Duration,
   predicate: (T) -> Boolean,
   f: () -> T
): T = until(duration, 1.seconds.fixed(), predicate, UntilListener.noop, f)

@OptIn(ExperimentalTime::class)
suspend fun <T> until(
   duration: Duration,
   interval: Interval,
   predicate: (T) -> Boolean,
   f: () -> T
): T = until(duration, interval, predicate = predicate, listener = UntilListener.noop, f = f)

/**
 * Executes a function until the given predicate returns true or the duration elapses.
 *
 * @param f the function to execute
 * @param predicate passed the result of the function f to evaluate if successful
 * @param listener notified on each invocation of f
 * @param duration the maximum amount of time to continue trying for success
 * @param interval the delay between invocations
 */
@OptIn(ExperimentalTime::class)
suspend fun <T> until(
   duration: Duration,
   interval: Interval,
   predicate: (T) -> Boolean,
   listener: UntilListener<T>,
   f: () -> T
): T {
   val end = TimeSource.Monotonic.markNow().plus(duration)
   var count = 0
   while (end.hasNotPassedNow()) {
      val result = f()
      if (predicate(result)) {
         return result
      } else {
         listener.onEval(result)
         count++
      }
      delay(interval.next(count).toLongMilliseconds())
   }
   throw failure("Test failed after ${duration.toLongMilliseconds()}ms; attempted $count times")
}
