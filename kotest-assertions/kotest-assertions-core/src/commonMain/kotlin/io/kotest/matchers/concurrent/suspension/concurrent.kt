package io.kotest.matchers.concurrent.suspension

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.failure
import io.kotest.common.nonDeterministicTestTimeSource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

/**
 * Assert [operation] completes within [duration].
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any later assertions won't run.
 */
suspend fun <A> shouldCompleteWithin(
   duration: Duration,
   operation: suspend () -> A,
): A {
   contract { callsInPlace(operation, EXACTLY_ONCE) }

   try {
      return withTimeout(duration) {
         operation()
      }
   } catch (ex: TimeoutCancellationException) {
      throw failure(
         "Operation took longer than expected. " +
            "Expected that operation completed within $duration, but it took longer and was cancelled."
      )
   }
}

/**
 * Assert [operation] completes within [durationRange].
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any later assertions won't run.
 */
suspend fun <A> shouldCompleteBetween(
   durationRange: ClosedRange<Duration>,
   operation: suspend () -> A,
): A {
   contract { callsInPlace(operation, EXACTLY_ONCE) }

   val timeSource = nonDeterministicTestTimeSource()
   val (value, timeElapsed) = timeSource.measureTimedValue {
      shouldCompleteWithin(durationRange.endInclusive) {
         operation()
      }
   }

   if (durationRange.start > timeElapsed) {
      throw failure(
         "Operation completed too quickly. " +
            "Expected that operation lasted at least ${durationRange.start}, but it took $timeElapsed."
      )
   }

   return value
}

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
   io.kotest.assertions.async.shouldTimeout(duration, operation)
}
