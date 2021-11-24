package io.kotest.assertions.until

import io.kotest.assertions.failure
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

fun interface UntilListener<in T> {
   fun onEval(t: T)

   companion object {
      @Deprecated("UntilListener is a functional interface. Simply use a lambda")
      val noop = UntilListener<Any?> { }
   }
}

@Deprecated("UntilListener is a functional interface. Simply use a lambda")
fun <T> untilListener(f: (T) -> Unit) = UntilListener<T> { t -> f(t) }

data class PatienceConfig(
   val duration: Duration,
   val interval: Interval,
)

/**
 * Executes a function at 1 second intervals until it returns true, or until a specified duration has elapsed.
 * If the duration elapses without the function returning true, an error will be thrown.
 *
 * @param duration the duration before an error is thrown
 * @param f the function to evaluate.
 *
 * This method supports suspension.
 */
suspend fun until(duration: Duration, f: suspend () -> Boolean) =
   until(duration, interval = 1.seconds.fixed(), f = f)

/**
 * Executes a function at a given interval until it returns true, or until a specified duration has elapsed.
 * If the duration elapses without the function returning true, an error will be thrown.
 *
 * @param duration the duration before an error is thrown
 * @param interval the delay between repeated invocations
 * @param f the function to evaluate.
 *
 * This method supports suspension.
 */
suspend fun until(duration: Duration, interval: Interval, f: suspend () -> Boolean) =
   until(duration = duration, interval = interval, predicate = { it }, f = f)

/**
 * Executes a function at a given interval until it returns true, or until a specified duration has elapsed.
 * If the duration elapses without the function returning true, an error will be thrown.
 *
 * @param patience specifies the duration and interval.
 * @param f the function to evaluate
 *
 * This method supports suspension.
 */
suspend fun until(patience: PatienceConfig, f: suspend () -> Boolean) =
   until(duration = patience.duration, interval = patience.interval, predicate = { it }, f = f)

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

/**
 * Executes a producer function at a given interval until the given predicate returns true for the last
 * produced value, or until a specified duration has elapsed. If the duration elapses without the predicate
 * returning true, an error will be thrown.
 *
 * @param patience specifies the duration and interval.
 * @param predicate a predicate that should return true if the last produced value is valid
 * @param f the producer function
 *
 * This method supports suspension.
 */
suspend fun <T> until(
   patience: PatienceConfig,
   predicate: suspend (T) -> Boolean,
   f: suspend () -> T
): T = until(duration = patience.duration, interval = patience.interval, predicate = predicate, listener = {}, f = f)

@Deprecated("Simply move the listener code into the predicate code. Will be removed in 6.0")
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
