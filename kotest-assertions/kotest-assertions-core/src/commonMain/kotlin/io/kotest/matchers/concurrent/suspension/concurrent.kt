package io.kotest.matchers.concurrent.suspension

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.failure
import io.kotest.common.nonDeterministicTestTimeSource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.measureTimedValue

/**
 * Assert [operation] completes within [duration].
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any later assertions won't run.
 */
suspend fun <A> shouldCompleteWithin(duration: Duration, operation: suspend () -> A): A {
   try {
      return withTimeout(duration) {
         operation()
      }
   } catch (ex: TimeoutCancellationException) {
      throw failure("Operation should have completed within $duration")
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
   try {
      val timeSource = nonDeterministicTestTimeSource()
      val (value, timeElapsed) = timeSource.measureTimedValue {
         withTimeout(durationRange.endInclusive) {
            operation()
         }
      }

      if (durationRange.start > timeElapsed) {
         throw failure("Operation should not have completed before ${durationRange.start}")
      }

      return value

   } catch (ex: TimeoutCancellationException) {
      throw failure("Operation should have completed within $durationRange")
   }
}

/**
 * Assert [operation] does not complete within [duration].
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any later assertions won't run.
 */
suspend fun <A> shouldTimeout(duration: Duration, operation: suspend () -> A) {
   try {
      withTimeout(duration) {
         operation()
      }
      throw failure("Operation should not have completed before $duration")
   } catch (_: TimeoutCancellationException) {
      // ignore timeout
   }
}
