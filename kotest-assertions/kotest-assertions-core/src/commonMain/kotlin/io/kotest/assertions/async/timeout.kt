package io.kotest.assertions.async

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.common.testTimeSource
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.measureTime

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
   contract { callsInPlace(operation, AT_MOST_ONCE) }

   try {
      val timeSource = testTimeSource()
      val timeElapsed = timeSource.measureTime {
         withTimeout(duration) {
            operation()
         }
      }
      AssertionErrorBuilder.fail(
         "Operation completed too quickly. " +
            "Expected that operation completed faster than $duration, but it took $timeElapsed."
      )
   } catch (_: TimeoutCancellationException) {
      // ignore timeout
   }
}
