package io.kotest.assertions

import io.kotest.mpp.bestName
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Retry the given lambda [f] in case of assertion error[AssertionError] and exception [Exception].
 * Stops trying when [maxRetry] or [timeout] is reached.
 * */
@OptIn(ExperimentalTime::class)
suspend fun <T> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = timeout,
   multiplier: Int = 1,
   f: () -> T
): T = retry(maxRetry, timeout, delay, multiplier, Exception::class, f)


/**
 * Retry the given lambda [f] in case of assertion error[AssertionError] and exception of type [exceptionClass].
 * Stops trying when [maxRetry] or [timeout] is reached. Or an exception other than [exceptionClass] is thrown
 * */
@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = timeout,
   multiplier: Int = 1,
   exceptionClass: KClass<E>,
   f: () -> T
): T {
   val mark = TimeSource.Monotonic.markNow()
   val end = mark.plus(timeout)
   var retrySoFar = 0
   var nextAwaitDuration = delay.toLongMilliseconds()
   var lastError: Throwable? = null

   while (end.hasNotPassedNow() && retrySoFar < maxRetry) {
      try {
         return f()
      } catch (e: Throwable) {
         when {
            // Not the kind of exceptions we were prepared to tolerate
            e::class.simpleName != "AssertionError" &&
               e::class != exceptionClass &&
               e::class.bestName() != "org.opentest4j.AssertionFailedError" -> throw e
         }
         lastError = e
         // else ignore and continue
      }
      retrySoFar++
      delay(nextAwaitDuration)
      nextAwaitDuration *= multiplier
   }
   val underlyingCause = if (lastError == null) "" else "; underlying cause was ${lastError.message}"
   throw Failures.failure(
      "Test failed after ${delay.toLong(DurationUnit.SECONDS)} seconds; attempted $retrySoFar times$underlyingCause",
      lastError
   )
}

