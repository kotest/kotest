package io.kotest.matchers.concurrent.suspension

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.common.testTimeSource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTimedValue

/**
 * Assert [operation] completes within [duration].
 *
 * Note: When invoked within [assertSoftly], wrap it in [shouldNotThrowAny], as follows:
 * assertSoftly {
 *   shouldNotThrowAny {
 *     shouldCompleteWithin(4.seconds) {
 *             delay(3.seconds)
 *          }
 *   }
 *   (2+2) shouldBe 4
 * }
 *
 * */
suspend fun <A> shouldCompleteWithin(
   duration: Duration,
   operation: suspend () -> A,
): A {
   contract { callsInPlace(operation, EXACTLY_ONCE) }

   try {
      return withTimeout(duration) {
         operation()
      }
   } catch (_: TimeoutCancellationException) {
      AssertionErrorBuilder.fail(
         "Operation took longer than expected. " +
            "Expected that operation completed within $duration, but it took longer and was cancelled."
      )
   }
}

/**
 * Assert [operation] completes within [durationRange].
 *
 * Note: When invoked within [assertSoftly], wrap it in [shouldNotThrowAny], as follows:
 * assertSoftly {
 *   shouldNotThrowAny {
 *     shouldCompleteWithin(1.seconds..2.seconds) {
 *             delay(1.5.seconds)
 *          }
 *   }
 *   (2+2) shouldBe 4
 * }
 */
suspend fun <A> shouldCompleteBetween(
   durationRange: ClosedRange<Duration>,
   operation: suspend () -> A,
): A {
   contract { callsInPlace(operation, EXACTLY_ONCE) }

   val timeSource = testTimeSource()
   val (value, timeElapsed) = timeSource.measureTimedValue {
      shouldCompleteWithin(durationRange.endInclusive) {
         operation()
      }
   }

   if (durationRange.start > timeElapsed) {
      AssertionErrorBuilder.fail(
         "Operation completed too quickly. " +
            "Expected that operation lasted at least ${durationRange.start}, but it took $timeElapsed."
      )
   }

   return value
}

/**
 * Assert [operation] does not complete within [duration].
 *
 * Note: When invoked within [assertSoftly], wrap it in [shouldNotThrowAny], as follows:
 * assertSoftly {
 *   shouldNotThrowAny {
 *     shouldTimeout(1.2.seconds) {
 *             delay(1.1.seconds)
 *          }
 *   }
 *   (2+2) shouldBe 4
 * }
 */
suspend fun shouldTimeout(
   duration: Duration,
   operation: suspend () -> Unit,
) {
   io.kotest.assertions.async.shouldTimeout(duration, operation)
}
