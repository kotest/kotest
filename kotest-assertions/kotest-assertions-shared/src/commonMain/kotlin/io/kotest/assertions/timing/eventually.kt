package io.kotest.assertions.timing

import io.kotest.assertions.SuspendingPredicate
import io.kotest.assertions.SuspendingProducer
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.*

/**
 * Runs a function until it doesn't throw as long as the specified duration hasn't passed
 */
@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, f: SuspendingProducer<T>): T =
   eventually(EventuallyConfig(duration = duration, exceptionClass = Throwable::class), f = f)

/**
 * Runs a function until it doesn't throw and the result satisfies the predicate as long as the specified duration hasn't passed
 */
@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   eventually(EventuallyConfig(duration = duration, exceptionClass = Throwable::class), predicate, f)

@OptIn(ExperimentalTime::class)
@Deprecated("""
Use eventually with an interval, using Duration based poll is deprecated.
To convert an existing duration to an interval you can Duration.fixed(), Duration.exponential(), or Duration.fibonacci().
""",
   ReplaceWith(
      "eventually(duration, interval = poll.fixed(), f = f)",
      "io.kotest.assertions.until.fixed"
   ))
suspend fun <T> eventually(duration: Duration, poll: Duration, f: SuspendingProducer<T>): T =
   eventually(EventuallyConfig(duration = duration, interval = poll.fixed(), exceptionClass = Throwable::class), f = f)

/**
 * Runs a function until it doesn't throw the specified exception as long as the specified duration hasn't passed
 */
@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: KClass<E>, f: SuspendingProducer<T>): T =
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
@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(
   duration: Duration = Duration.INFINITE,
   interval: Interval = 25.milliseconds.fixed(),
   listener: EventuallyListener<T> = EventuallyListener.noop,
   retries: Int = Int.MAX_VALUE,
   exceptionClass: KClass<Throwable>? = Throwable::class,
   predicate: SuspendingPredicate<T> = { true },
   f: SuspendingProducer<T>
): T = eventually(EventuallyConfig(duration, interval, listener, retries, exceptionClass), predicate, f)

/**
 * Runs a function until it doesn't throw and the result satisfies the predicate as long as the specified duration hasn't passed
 * and uses [EventuallyConfig] to control the duration, interval, listener, retries, and exceptionClass.
 */
@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(
   config: EventuallyConfig<T, E>,
   predicate: SuspendingPredicate<T> = { true },
   f: SuspendingProducer<T>,
): T {
   val start = TimeSource.Monotonic.markNow()
   val end = start.plus(config.duration)
   var times = 0
   var firstError: Throwable? = null
   var lastError: Throwable? = null

   while (end.hasNotPassedNow() && times < config.retries) {
      try {
         val result = f()
         config.listener.onEval(result, EventuallyState(start, end, times, firstError, lastError))
         if (predicate(result)) {
            return result
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
      delay(config.interval.next(times))
   }

   val message = StringBuilder().apply {
      appendLine("Eventually block failed after ${config.duration}; attempted $times time(s); ${config.interval} delay between attempts")

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

@OptIn(ExperimentalTime::class)
data class EventuallyConfig<T, E : Throwable> (
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val listener: EventuallyListener<T> = EventuallyListener.noop,
   val retries: Int = Int.MAX_VALUE,
   val exceptionClass: KClass<E>? = null,
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
   }
}

@OptIn(ExperimentalTime::class)
data class EventuallyState (
   val start: TimeMark, val end: TimeMark, val times: Int, val firstError: Throwable?, val lastError: Throwable?,
)

fun interface EventuallyListener<in T> {
   fun onEval(t: T, state: EventuallyState)

   companion object {
      val noop = EventuallyListener<Any?> { _, _ -> }
   }
}
