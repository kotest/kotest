package io.kotest

import io.kotest.assertions.Failures
import kotlinx.coroutines.delay
import java.time.Duration

/**
 * Retry the given lambda [f] in case of assertion error[AssertionError] and exception [Exception].
 * Stops trying when [maxRetry] or [timeout] is reached.
 * */
suspend fun <T> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = timeout,
   multiplier: Int = 1,
   f: () -> T
): T = retry(maxRetry, timeout, delay, multiplier, Exception::class.java, f)


/**
 * Retry the given lambda [f] in case of assertion error[AssertionError] and exception of type [exceptionClass].
 * Stops trying when [maxRetry] or [timeout] is reached. Or an exception other than [exceptionClass] is thrown
 * */
suspend fun <T, E : Throwable> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = timeout,
   multiplier: Int = 1,
   exceptionClass: Class<E>,
   f: () -> T
): T {
   val end = System.currentTimeMillis() + timeout.toMillis()
   var retrySoFar = 0
   var nextAwaitDuration = delay.toMillis()
   var lastError: Throwable? = null

   while (System.currentTimeMillis() < end && retrySoFar < maxRetry) {
      try {
         return f()
      } catch (e: Throwable) {
         if (!exceptionClass.isAssignableFrom(e.javaClass) && !AssertionError::class.java.isAssignableFrom(e.javaClass)) {
            // Not the kind of exception we were prepared to tolerate
            throw e
         }
         lastError = e
         // else ignore and continue
      }
      retrySoFar++
      delay(nextAwaitDuration)
      nextAwaitDuration *= multiplier
   }
   val underlyingCause = if (lastError == null) "" else "; underlying cause was ${lastError.localizedMessage}"
   throw Failures.failure(
      "Test failed after ${delay.seconds} seconds; attempted $retrySoFar times$underlyingCause",
      lastError
   )
}

