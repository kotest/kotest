package io.kotest.framework.concurrency

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

@ExperimentalKotest
data class EventuallyConfig(
   val duration: Millis = defaultDuration,
   val interval: Interval = 25L.fixed(),
   val retries: Int = Int.MAX_VALUE,
   val exceptions: Set<KClass<out Throwable>> = setOf(),
) {
   init {
      require(retries > 0) { "Retries should not be less than one" }
      require(duration > 0L) { "Duration cannot be negative" }
   }

   companion object {
      const val defaultDuration: Millis = 3_600_000L
   }
}

@ExperimentalKotest
data class EventuallyState<T>(
   val result: T?,
   val start: Instant,
   val end: Instant,
   val iteration: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

@ExperimentalKotest
fun interface EventuallyListener<T> {
   fun onEval(state: EventuallyState<T>): Boolean

   companion object {
      val default = EventuallyListener<Any?> { it.thisError == null }
   }
}

@ExperimentalKotest
suspend fun <T> eventually(
   duration: Millis,
   interval: Interval = 25L.fixed(),
   retries: Int = Int.MAX_VALUE,
   exceptions: Set<KClass<out Throwable>> = setOf(),
   listener: EventuallyListener<T>? = null,
   f: ConcurrencyProducer<T>
): T =
   eventually(EventuallyConfig(duration, interval, retries, exceptions), listener, f)

@ExperimentalKotest
suspend fun <T> eventually(
   config: EventuallyConfig = EventuallyConfig(),
   listener: EventuallyListener<T>? = null,
   f: ConcurrencyProducer<T>
): T {
   val start = Instant(timeInMillis())
   val end = Instant(timeInMillis() + config.duration)
   var times = 0
   var firstError: Throwable? = null
   var lastError: Throwable? = null
   var predicateFailedTimes = 0
   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   var lastDelayPeriod: Millis = 0L
   var lastInterval: Millis = 0L

   fun attemptsLeft() = timeInMillis() < end.timeInMillis && times < config.retries

   // if we only executed once, and the last delay was > last interval, we didn't get a chance to run again
   // so we run once more before exiting
   fun isLongWait() = times == 1 && lastDelayPeriod > lastInterval

   while (attemptsLeft() || isLongWait()) {
      try {
         val result = f()
         if (listener != null) {
            val success = listener.onEval(EventuallyState(result, start, end, times, firstError, lastError))
            if (success) {
               errorCollector.setCollectionMode(originalAssertionMode)
               return result
            } else {
               predicateFailedTimes++
            }
         } else {
            errorCollector.setCollectionMode(originalAssertionMode)
            return result
         }
      } catch (e: Throwable) {
         if (AssertionError::class.isInstance(e) || config.exceptions.any { it.isInstance(e) }) {
            if (firstError == null) {
               firstError = e
            } else {
               lastError = e
            }
            listener?.onEval(EventuallyState(null, start, end, times, firstError, lastError))
         } else {
            throw e
         }
      }
      times++
      lastInterval = config.interval.next(times)
      val delayMark = timeInMillis()
      delay(lastInterval)
      lastDelayPeriod = timeInMillis() - delayMark
   }

   errorCollector.setCollectionMode(originalAssertionMode)

   val message = StringBuilder().apply {
      appendLine("Eventually block failed after ${config.duration}ms; attempted $times time(s); ${config.interval} delay between attempts")

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
