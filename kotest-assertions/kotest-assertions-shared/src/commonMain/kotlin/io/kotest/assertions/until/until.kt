package io.kotest.assertions.until

import io.kotest.assertions.failure
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.seconds
import kotlinx.coroutines.delay

fun interface UntilListener<in T> {
   fun onEval(t: T)

   companion object {
      @Deprecated("UntilListener is a functional interface. Simply use a lambda")
      val noop = UntilListener<Any?> { }
   }
}

@Deprecated("UntilListener is a functional interface. Simply use a lambda")
fun <T> untilListener(f: (T) -> Unit) = UntilListener<T> { t -> f(t) }

/**
 * Executes the function [f] at a fixed interval until it returns true, or until the [duration] has elapsed.
 *
 * If the duration elapses without the function returning true, an error will be thrown.
 *
 * This method supports suspension.
 */
suspend fun until(duration: Duration, f: suspend () -> Boolean) =
   until(duration, interval = 1.seconds.fixed(), f = f)

/**
 * Executes the function [f] at a given [interval] until it returns true, or until the [duration] has elapsed.
 *
 * If the duration elapses without the function returning true, an error will be thrown.
 *
 * This method supports suspension.
 *
 * This method supports suspension.
 */
suspend fun until(duration: Duration, interval: Interval, f: suspend () -> Boolean) =
   until(duration = duration, interval = interval, predicate = { it }, f = f)

/**
 * Executes the function [f] at a fixed interval until it returns a value that passes the given [predicate],
 * or until the [duration] has elapsed.
 *
 * If the duration elapses without the predicate returning true, an error will be thrown.
 *
 * This method supports suspension.
 */
suspend fun <T> until(
   duration: Duration,
   predicate: suspend (T) -> Boolean,
   f: suspend () -> T
): T = until(duration, interval = 1.seconds.fixed(), predicate = predicate, f = f)

/**
 * Executes the function [f] at a given [interval] until it returns a value that passes the given [predicate],
 * or until the [duration] has elapsed.
 *
 * If the duration elapses without the predicate returning true, an error will be thrown.
 *
 * This method supports suspension.
 */
suspend fun <T> until(
   duration: Duration,
   interval: Interval,
   predicate: suspend (T) -> Boolean,
   f: suspend () -> T
): T = until(duration = duration, interval = interval, predicate = predicate, listener = {}, f = f)

@Deprecated("Simply move the listener code into the predicate code. Will be removed in 4.7 or 5.0")
suspend fun <T> until(
   duration: Duration,
   interval: Interval = 1.seconds.fixed(),
   predicate: suspend (T) -> Boolean,
   listener: UntilListener<T>,
   f: suspend () -> T
): T {

   val start = TimeSource.Monotonic.markNow()
   val end = start.plus(duration)
   var times = 0

   while (end.hasNotPassedNow()) {
      val result = f()
      listener.onEval(result)
      if (predicate.invoke(result)) {
         return result
      }
      times++
      delay(interval.next(times))
   }

   val runtime = start.elapsedNow()
   val message = "Until block failed after ${runtime}; attempted $times time(s); $interval delay between attempts"
   throw failure(message)
}
