package io.kotest.assertions.timing

import io.kotest.assertions.failure
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, f: suspend () -> T): T =
   eventually(duration, 25.milliseconds, Exception::class, f)

@OptIn(ExperimentalTime::class)
suspend fun <T> eventually(duration: Duration, poll: Duration, f: suspend () -> T): T =
   eventually(duration, poll, Exception::class, f)

@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(
   duration: Duration,
   exceptionClass: KClass<E>,
   f: suspend () -> T
): T = eventually(duration, 25.milliseconds, exceptionClass, f)

@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> eventually(
   duration: Duration,
   poll: Duration,
   exceptionClass: KClass<E>,
   f: suspend () -> T
): T {
   val end = TimeSource.Monotonic.markNow().plus(duration)
   var times = 0
   var firstError: Throwable? = null
   var lastError: Throwable? = null
   while (end.hasNotPassedNow()) {
      try {
         return f()
      } catch (e: Throwable) {
         // we only accept exceptions of type exceptionClass and AssertionError
         // if we didn't accept AssertionError then a matcher failure would immediately fail this function
         if (!exceptionClass.isInstance(e) && !AssertionError::class.isInstance(e))
            throw e
         if (firstError == null)
            firstError = e
         else
            lastError = e
      }
      times++
      delay(poll.toLongMilliseconds())
   }

   val sb = StringBuilder()
   sb.append("Eventually block failed after ${duration}; attempted $times time(s); $poll delay between attempts")
   sb.append("\n")
   sb.append("\n")

   if (firstError != null) {
      sb.append("The first error was caused by: ${firstError.message}")
      sb.append("\n")
      sb.append(firstError.toString())
      sb.append("\n")
   }

   if (lastError != null) {
      sb.append("The last error was caused by: ${lastError.message}")
      sb.append("\n")
      sb.append(lastError.toString())
      sb.append("\n")
   }

   throw failure(
      sb.toString()
   )
}
