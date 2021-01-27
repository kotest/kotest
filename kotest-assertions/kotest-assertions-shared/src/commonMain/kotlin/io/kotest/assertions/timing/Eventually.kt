package io.kotest.assertions.timing

import io.kotest.assertions.NondeterministicListener
import io.kotest.assertions.SuspendingPredicate
import io.kotest.assertions.SuspendingProducer
import io.kotest.assertions.failure
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fixed
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
data class Eventually<T, E : Throwable> (
   val duration: Duration = Duration.INFINITE,
   val interval: Interval = 25.milliseconds.fixed(),
   val listener: NondeterministicListener<T> = NondeterministicListener.noop,
   val retries: Int = Int.MAX_VALUE,
   val exceptionClass: KClass<E>? = null,
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
   }

   fun withInterval(interval: Interval) = this.copy(interval = interval)
   fun withListener(listener: NondeterministicListener<T>) = this.copy(listener = listener)
   fun withRetries(retries: Int) = this.copy(retries = retries)

   suspend operator fun invoke(
      predicate: SuspendingPredicate<T> = { true },
      f: SuspendingProducer<T>,
   ): T {
      val end = TimeSource.Monotonic.markNow().plus(duration)
      var times = 0
      var firstError: Throwable? = null
      var lastError: Throwable? = null
      while (end.hasNotPassedNow() && times < retries) {
         try {
            val result = f()
            listener.onEval(result)
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
         append("Eventually block failed after ${duration}; attempted $times time(s); $interval delay between attempts")

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
suspend fun <T> eventually(
   duration: Duration = Duration.INFINITE,
   interval: Interval = 25.milliseconds.fixed(),
   listener: NondeterministicListener<T> = NondeterministicListener.noop,
   retries: Int = Int.MAX_VALUE,
   exceptionClass: KClass<Exception> = Exception::class,
   predicate: SuspendingPredicate<T> = { true },
   f: SuspendingProducer<T>
): T = Eventually(duration, interval, listener, retries, exceptionClass).invoke(predicate, f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, interval: Interval, f: SuspendingProducer<T>): T =
   eventually(duration, interval, f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   eventually(duration, predicate = predicate, f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: KClass<E>, f: SuspendingProducer<T>): T =
   eventually(duration, exceptionClass = exceptionClass, f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, interval: Interval, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   eventually(duration, interval, predicate = predicate, f = f)

@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(duration: Duration, interval: Interval, exceptionClass: KClass<E>, f: SuspendingProducer<T>): T =
   eventually(duration, interval, exceptionClass = exceptionClass, f = f)

@OptIn(ExperimentalTime::class)
@Deprecated("Use eventually with an interval, using Duration based poll is deprecated",
   ReplaceWith("eventually(duration, poll.fixed(), f = f)", "io.kotest.assertions.until.fixed")
)
suspend fun <T> eventually(duration: Duration, poll: Duration, f: SuspendingProducer<T>): T =
   eventually(duration, poll.fixed(), f = f)

@OptIn(ExperimentalTime::class)
@Deprecated("Use eventually with an interval, using Duration based poll is deprecated",
   ReplaceWith("eventually(duration, poll.fixed(), predicate = predicate, f = f)", "io.kotest.assertions.until.fixed"))
suspend fun <T> eventually(duration: Duration, poll: Duration, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   eventually(duration, poll.fixed(), predicate = predicate, f = f)

@OptIn(ExperimentalTime::class)
@Deprecated("Use eventually with an interval, using Duration based poll is deprecated",
   ReplaceWith("eventually(duration, poll.fixed(), exceptionClass = exceptionClass, f = f)", "io.kotest.assertions.until.fixed"))
suspend fun <T, E : Throwable> eventually(duration: Duration, poll: Duration, exceptionClass: KClass<E>, f: suspend () -> T): T =
   eventually(duration, poll.fixed(), exceptionClass = exceptionClass, f = f)

