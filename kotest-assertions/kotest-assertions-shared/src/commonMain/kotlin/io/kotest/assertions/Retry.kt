package io.kotest.assertions

import io.kotest.mpp.bestName
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.seconds
import kotlinx.coroutines.delay

/**
 * Retry [f] until it's a success or [maxRetry]/[timeout] is reached
 * 
 * This will treat any Exception as a failure, along with [AssertionError].
 * 
 * Retry delay might increase exponentially if you choose a [multiplier] value. For example, if you want to configure
 * 5 [maxRetry], with an initial [delay] of 1s between requests, the delay between requests will increase when you
 * choose 2 as your [multiplier]:
 * 1 - Failed (wait 1s before retrying)
 * 2 - Failed (wait 2s before retrying)
 * 3 - Failed (wait 4s before retrying)
 * ..
 * 
 * If either timeout or max retries is reached, the execution will be aborted and an exception will be thrown.
 * 
 * */
@OptIn(ExperimentalTime::class)
suspend fun <T> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = 1.seconds,
   multiplier: Int = 1,
   f: suspend () -> T
): T = retry(maxRetry, timeout, delay, multiplier, Exception::class, f)


/**
 * Retry [f] until it's a success or [maxRetry]/[timeout] is reached
 *
 * This will treat only [exceptionClass] as a failure, along with [AssertionError].
 *
 * Retry delay might increase exponentially if you choose a [multiplier] value. For example, if you want to configure
 * 5 [maxRetry], with an initial [delay] of 1s between requests, the delay between requests will increase when you
 * choose 2 as your [multiplier]:
 * 1 - Failed (wait 1s before retrying)
 * 2 - Failed (wait 2s before retrying)
 * 3 - Failed (wait 4s before retrying)
 * ..
 *
 * If either timeout or max retries is reached, the execution will be aborted and an exception will be thrown.
 *
 * */
@OptIn(ExperimentalTime::class)
suspend fun <T, E : Throwable> retry(
   maxRetry: Int,
   timeout: Duration,
   delay: Duration = 1.seconds,
   multiplier: Int = 1,
   exceptionClass: KClass<E>,
   f: suspend () -> T
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
   throw failure(
      "Test failed after ${delay.toLong(DurationUnit.SECONDS)} seconds; attempted $retrySoFar times$underlyingCause",
      lastError
   )
}

