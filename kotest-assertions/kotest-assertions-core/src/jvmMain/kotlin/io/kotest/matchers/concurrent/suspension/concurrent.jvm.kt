package io.kotest.matchers.concurrent.suspension

import io.kotest.assertions.assertSoftly
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration
import kotlin.time.toDurationUnit

/**
 * Asserts that the given suspendable lambda completes within the given time.
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any subsequent assertion won't run.
 */
@Deprecated(
   "Updated to use Kotlin time types. This function will be removed in 6.0.",
   ReplaceWith(
      "shouldCompleteWithin(timeout.toDuration(unit.toDurationUnit()), thunk)",
      "kotlin.time.toDuration",
      "kotlin.time.toDurationUnit"
   )
)
suspend fun <A> shouldCompleteWithin(timeout: Long, unit: TimeUnit, thunk: suspend () -> A): A {
   return shouldCompleteWithin(timeout.toDuration(unit.toDurationUnit()), thunk)
}

/**
 * Asserts that the given suspendable lambda completes within the given time range(inclusive of [from] and [to]).
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any subsequent assertion won't run.
 */
@Deprecated(
   "Updated to use Kotlin time types. This function will be removed in 6.0.",
   ReplaceWith(
      "shouldCompleteBetween(from.toDuration(unit.toDurationUnit())..to.toDuration(unit.toDurationUnit()), thunk)",
      "kotlin.time.toDuration",
      "kotlin.time.toDurationUnit",
      "kotlin.time.toDuration",
      "kotlin.time.toDurationUnit"
   ),
)
suspend fun <A> shouldCompleteBetween(from: Long, to: Long, unit: TimeUnit, thunk: suspend () -> A): A {
   return shouldCompleteBetween(
      durationRange = from.toDuration(unit.toDurationUnit())..to.toDuration(unit.toDurationUnit()),
      thunk = thunk,
   )
}

/**
 * Asserts that the given suspendable lambda completes does not complete within given time.
 *
 * Note: It does not work well within [assertSoftly].
 * If used within [assertSoftly] and this assertion fails, any subsequent assertion won't run.
 */
@Deprecated(
   "Updated to use Kotlin time types. This function will be removed in 6.0.",
   ReplaceWith(
      "shouldTimeout(timeout.toDuration(unit.toDurationUnit()), thunk)",
      "kotlin.time.toDuration",
      "kotlin.time.toDurationUnit"
   )
)
suspend fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: suspend () -> A) {
   shouldTimeout(timeout.toDuration(unit.toDurationUnit()), thunk)
}
