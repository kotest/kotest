package io.kotest.assertions.async

import io.kotest.assertions.failure
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
suspend fun <A> shouldTimeout(duration: Duration, thunk: suspend () -> A) =
   shouldTimeout(duration.toLongMilliseconds(), TimeUnit.MILLISECONDS, thunk)

suspend fun <A> shouldTimeout(duration: java.time.Duration, thunk: suspend () -> A) =
   shouldTimeout(duration.toMillis(), TimeUnit.MILLISECONDS, thunk)

/**
 * Verify that an asynchronous call should timeout
 */
suspend fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: suspend () -> A) {
   val timedOut = try {
      withTimeout(unit.toMillis(timeout)) {
         thunk()
         false
      }
   } catch (t: TimeoutCancellationException) {
      true
   }
   if (!timedOut) {
      throw failure("Expected test to timeout for $timeout/$unit")
   }
}
