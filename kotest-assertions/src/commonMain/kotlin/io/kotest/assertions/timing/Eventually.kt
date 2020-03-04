package io.kotest.assertions.timing

import io.kotest.assertions.Failures
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@UseExperimental(ExperimentalTime::class)
inline fun <T> eventually(duration: Duration, f: () -> T): T = eventually(duration, Exception::class, f)

@UseExperimental(ExperimentalTime::class)
inline fun <T, E : Throwable> eventually(duration: Duration, exceptionClass: KClass<E>, f: () -> T): T {
   val end = TimeSource.Monotonic.markNow().plus(duration)
   var times = 0
   var lastError: Throwable? = null
   while (end.hasNotPassedNow()) {
      try {
         return f()
      } catch (e: Throwable) {
         // we only accept exceptions of type exceptionClass and AssertionError
         // if we didn't accept AssertionError then a matcher failure would immediately fail this function
         if (!exceptionClass.isInstance(e) && !AssertionError::class.isInstance(e))
            throw e
         lastError = e
      }
      times++
   }
   val underlyingCause = if (lastError == null) "" else "; underlying cause was ${lastError.message}"
   throw Failures.failure(
      "Test failed after ${duration.toLongMilliseconds()}ms; attempted $times times$underlyingCause",
      lastError
   )
}
