@file:Suppress("DEPRECATION")

package io.kotest.assertions.timing

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import io.kotest.common.testTimeSource
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

/**
 * Runs a function until it doesn't throw as long as the specified duration hasn't passed
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(duration: Duration, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration = duration), f = f)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T : Any> eventually(
   duration: Duration,
   interval: Interval,
   f: suspend () -> T
): T = eventually(EventuallyConfig(duration, interval), f = f)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   duration: Duration,
   interval: Interval,
   predicate: EventuallyPredicate<T>,
   f: suspend () -> T,
): T = eventually(EventuallyConfig(duration = duration, interval), predicate = predicate, f = f)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   duration: Duration,
   interval: Interval,
   listener: EventuallyListener<T>,
   f: suspend () -> T,
): T = eventually(EventuallyConfig(duration = duration, interval), listener = listener, f = f)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(duration: Duration, poll: Duration, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration = duration, interval = poll.fixed()), f = f)

/**
 * Runs a function until it doesn't throw the specified exception as long as the specified duration hasn't passed
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(duration: Duration, exceptionClass: KClass<out Throwable>, f: suspend () -> T): T =
   eventually(EventuallyConfig(duration = duration, exceptionClass = exceptionClass), f = f)

/**
 * Runs a function until the following constraints are eventually met:
 * the optional [predicate] must be satisfied, defaults to true
 * the optional [duration] has not passed now, defaults to [Duration.INFINITE]
 * the number of iterations does not exceed the optional [retries], defaults to [Int.MAX_VALUE]
 *
 * [eventually] will catch the specified optional [exceptionClass] and (or when not specified) [AssertionError], defaults to [Throwable]
 * [eventually] will delay the specified [interval] between iterations, defaults to 25 [milliseconds]
 * [eventually] will pass the resulting value and state (see [EventuallyState]) into the optional [listener]
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
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
 * Runs a function until it doesn't throw and the result satisfies the predicate as long as the specified duration hasn't passed
 * and uses [EventuallyConfig] to control the duration, interval, listener, retries, and exceptionClass.
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   config: EventuallyConfig,
   predicate: EventuallyPredicate<T> = { true },
   listener: EventuallyListener<T> = EventuallyListener { },
   f: suspend () -> T,
): T {

   val start = testTimeSource().markNow()
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
            listener.onEval(EventuallyState(null, start, end, times, firstError, lastError))
         } else {
            throw e
         }
      }
      times++
      lastInterval = config.interval.next(times)
      val delayMark = testTimeSource().markNow()
      delay(lastInterval)
      lastDelayPeriod = delayMark.elapsedNow()
   }

   errorCollector.setCollectionMode(originalAssertionMode)

   val message = buildString {
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

   throw failure(message)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class EventuallyConfig(
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val retries: Int = Int.MAX_VALUE,
   val exceptionClass: KClass<out Throwable>? = Throwable::class,
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
      require(!duration.isNegative()) { "Duration cannot be negative" }
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class EventuallyState<T>(
   val result: T?,
   val start: TimeMark,
   val end: TimeMark,
   val iteration: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
typealias EventuallyPredicate<T> = (T) -> Boolean

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun interface EventuallyListener<T> {
   fun onEval(state: EventuallyState<T>)
}
