package io.kotest.assertions.timing

import io.kotest.assertions.SuspendingPredicate
import io.kotest.assertions.SuspendingProducer
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.*

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, f: SuspendingProducer<T>): T =
   Eventually<T, Throwable>(duration = duration, exceptionClass = Throwable::class).invoke(f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   Eventually<T, Throwable>(duration = duration, exceptionClass = Throwable::class).invoke(predicate, f)

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
   Eventually<T, Throwable>(duration = duration, interval = poll.fixed(), exceptionClass = Throwable::class).invoke(f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: KClass<E>, f: SuspendingProducer<T>): T =
   Eventually<T, E>(duration = duration, exceptionClass = exceptionClass).invoke(f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(
   duration: Duration = Duration.INFINITE,
   interval: Interval = 25.milliseconds.fixed(),
   listener: EventuallyListener<T> = EventuallyListener.noop,
   retries: Int = Int.MAX_VALUE,
   exceptionClass: KClass<Throwable>? = Throwable::class,
   predicate: SuspendingPredicate<T> = { true },
   f: SuspendingProducer<T>
): T = Eventually(duration, interval, listener, retries, exceptionClass).invoke(predicate, f)

@OptIn(ExperimentalTime::class)
data class Eventually<T, E : Throwable> (
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val listener: EventuallyListener<T> = EventuallyListener.noop,
   val retries: Int = Int.MAX_VALUE,
   val exceptionClass: KClass<E>? = null,
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
   }

   fun withInterval(interval: Interval) = this.copy(interval = interval)
   fun withListener(listener: EventuallyListener<T>) = this.copy(listener = listener)
   fun withRetries(retries: Int) = this.copy(retries = retries)

   suspend operator fun invoke(
      predicate: SuspendingPredicate<T> = { true },
      f: SuspendingProducer<T>,
   ): T {
      val start = TimeSource.Monotonic.markNow()
      val end = start.plus(duration)
      var times = 0
      var firstError: Throwable? = null
      var lastError: Throwable? = null

      while (end.hasNotPassedNow() && times < retries) {
         try {
            val result = f()
            listener.onEval(result, EventuallyState(start, end, times, firstError, lastError))
            if (predicate(result)) {
               return result
            }
         } catch (e: Throwable) {
            if (AssertionError::class.isInstance(e) || exceptionClass?.isInstance(e) == true) {
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
         delay(interval.next(times))
      }

      val message = StringBuilder().apply {
         appendLine("Eventually block failed after ${duration}; attempted $times time(s); $interval delay between attempts")

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
