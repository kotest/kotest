package io.kotest.assertions.async

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.failure
import io.kotest.common.testTimeSource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Assert [operation] does not complete within [duration].
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any later assertions won't run.
 */
suspend fun shouldTimeout(
   duration: Duration,
   operation: suspend () -> Unit,
) {
   contract { callsInPlace(operation, EXACTLY_ONCE) }

   try {
      val timeSource = testTimeSource()
      val timeElapsed = timeSource.measureTime {
         withTimeout(duration) {
            operation()
         }
      }
      throw failure(
         "Operation completed too quickly. " +
            "Expected that operation completed faster than $duration, but it took $timeElapsed."
      )
   } catch (_: TimeoutCancellationException) {
      // ignore timeout
   }
}
