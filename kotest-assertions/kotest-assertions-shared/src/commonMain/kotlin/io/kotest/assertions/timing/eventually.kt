package io.kotest.assertions.timing

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.milliseconds

/**
 * Runs function [f] until it doesn't throw, as long as the specified [duration] hasn't passed.
 * @return the result of [f] or fails if [f] never completed without throwing
 */
suspend fun <T> eventually(duration: Duration, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration), f = f)

/**
 * Runs function [f] with the specified [interval] until it doesn't throw, as long as the specified [duration] hasn't passed.
 * @return the result of [f] or fails if [f] never completed without throwing
 */
suspend fun <T : Any> eventually(
   duration: Duration,
   interval: Interval,
   f: suspend () -> T
): T = eventually(EventuallyConfig(duration, interval), f = f)

/**
 * Runs function [f] until it matches [predicate], as long as the specified [duration] hasn't passed.
 */
suspend fun <T> eventually(
   duration: Duration,
   interval: Interval,
   predicate: EventuallyPredicate<T>,
   f: suspend () -> T,
): T = eventually(EventuallyConfig(duration, interval), predicate = predicate, f = f)

/**
 * Runs function [f] with the specified [interval] until it doesn't throw, as long as the specified [duration] hasn't passed.
 * @param listener will be notified at every iteration
 * @return the result of [f] or fails if [f] never completed without throwing
 */
suspend fun <T> eventually(
   duration: Duration,
   interval: Interval,
   listener: EventuallyListener<T>,
   f: suspend () -> T,
): T = eventually(EventuallyConfig(duration, interval), listener = listener, f = f)

suspend fun <T> eventually(duration: Duration, poll: Duration, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration = duration, interval = poll.fixed()), f = f)

/**
 * Runs a function until it doesn't throw the specified exception as long as the specified duration hasn't passed
 */
suspend fun <T> eventually(duration: Duration, exceptionClass: KClass<out Throwable>, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration = duration, exceptionClass = exceptionClass), f = f)

/**
 * Runs a function until the following constraints are eventually met:
 * - the optional [predicate] must be satisfied, defaults to true
 * - the optional [duration] has not passed now, defaults to [Duration.INFINITE]
 * - the number of iterations does not exceed the optional [retries], defaults to [Int.MAX_VALUE]
 *
 * eventually will
 * - catch the specified optional [exceptionClass] and (or when not specified) [AssertionError]
 * - delay the specified [interval] between iterations, defaults to 25 [milliseconds]
 * - pass the resulting value and state (see [EventuallyState]) into the optional [listener]
 *
 * @return the first accepted result of [f]
 */
suspend fun <T> eventually(
   duration: Duration = Duration.INFINITE,
   interval: Interval = 25.milliseconds.fixed(),
   predicate: EventuallyPredicate<T> = { true },
   listener: EventuallyListener<T> = EventuallyListener { },
   retries: Int = Int.MAX_VALUE,
   exceptionClass: KClass<out Throwable>? = null,
   f: suspend () -> T,
): T = eventually(EventuallyConfig(duration, interval, retries, exceptionClass), predicate, listener, f)

/**
 * Runs a function until it doesn't throw and the result satisfies the predicate, as long as the specified duration hasn't passed.
 * @param config controls the duration, interval, listener, retries, and exceptionClass
 */
suspend fun <T> eventually(
   config: EventuallyConfig,
   predicate: EventuallyPredicate<T> = { true },
   listener: EventuallyListener<T> = EventuallyListener { },
   f: suspend () -> T,
): T {

   val start = TimeSource.Monotonic.markNow()
   val end = start.plus(config.duration)
   var times = 0
   var firstError: Throwable? = null
   var lastError: Throwable? = null
   var predicateFailedTimes = 0
   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   var lastDelayPeriod = Duration.ZERO
   var lastInterval = Duration.ZERO

   fun attemptsLeft() = end.hasNotPassedNow() && times < config.retries

   // if we only executed once, and the last delay was > last interval, we didn't get a chance to run again
   // so we run once more before exiting
   fun isLongWait() = times == 1 && lastDelayPeriod > lastInterval

   while (attemptsLeft() || isLongWait()) {
      try {
         val result = f()
         listener.onEval(EventuallyState(result, start, end, times, firstError, lastError))
         if (predicate(result)) {
            errorCollector.setCollectionMode(originalAssertionMode)
            return result
         } else {
            predicateFailedTimes++
         }
      } catch (e: Throwable) {
         if (AssertionError::class.isInstance(e) || config.exceptionClass?.isInstance(e) == true) {
            if (firstError == null) {
               firstError = e
            } else {
               lastError = e
            }
         } else {
            throw e
         }
      }
      times++
      lastInterval = config.interval.next(times)
      println("sleeping for $lastInterval")
      val delayMark = TimeSource.Monotonic.markNow()
      delay(lastInterval)
      lastDelayPeriod = delayMark.elapsedNow()
      println("actual sleep $lastDelayPeriod")
   }

   errorCollector.setCollectionMode(originalAssertionMode)

   val message = StringBuilder().apply {
      appendLine("Eventually block failed after ${config.duration}; attempted $times time(s); ${config.interval} delay between attempts")

      if (predicateFailedTimes > 0) {
         appendLine("The provided predicate failed $predicateFailedTimes times")
      }

      if (firstError != null) {
         appendLine("The first error was caused by: ${firstError.message}")
         appendLine(firstError.stackTraceToString())
      }

      if (lastError != null) {
         appendLine("The last error was caused by: ${lastError.message}")
         appendLine(lastError.stackTraceToString())
      }
   }

   throw failure(message.toString())
}

data class EventuallyConfig(
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val retries: Int = Int.MAX_VALUE,
   val exceptionClass: KClass<out Throwable>? = null,
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
      require(!duration.isNegative()) { "Duration cannot be negative" }
   }
}

data class EventuallyState<T>(
   val result: T,
   val start: TimeMark,
   val end: TimeMark,
   val iteration: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

typealias EventuallyPredicate<T> = (T) -> Boolean

fun interface EventuallyListener<T> {
   fun onEval(state: EventuallyState<T>)
}
