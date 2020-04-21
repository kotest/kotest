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
         lastError = e
      }
      times++
      delay(poll.toLongMilliseconds())
   }
   val underlyingCause =
      if (lastError == null) "" else "; first cause was ${firstError?.message}; last cause was ${lastError.message}"
   throw failure(
      "Test failed after ${duration.toLongMilliseconds()}ms; attempted $times times$underlyingCause",
      lastError
   )
}
