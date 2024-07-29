package io.kotest.assertions.async

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.time.toKotlinDuration

/**
 * Assert [thunk] does not complete within [duration].
 */
@Deprecated(
   "Updated to use Kotlin time types. This function will be removed in 6.0.",
   ReplaceWith(
      "shouldTimeout(duration.toKotlinDuration()) { thunk() }",
      "kotlin.time.toKotlinDuration",
   ),
)
suspend fun <A> shouldTimeout(duration: Duration, thunk: suspend () -> A): Unit =
   shouldTimeout(duration.toKotlinDuration()) { thunk() }

/**
 * Assert [thunk] does not complete within [timeout]/[unit].
 */
@Deprecated(
   "Updated to use Kotlin time types. This function will be removed in 6.0.",
   ReplaceWith(
      "shouldTimeout(Duration.of(timeout, unit.toChronoUnit())) { thunk() }",
      "java.time.Duration"
   ),
)
suspend fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: suspend () -> A) {
   @Suppress("DEPRECATION")
   shouldTimeout(Duration.of(timeout, unit.ChronoUnit())) { thunk() }
}


private fun TimeUnit.ChronoUnit(): ChronoUnit {
   return when (this) {
      TimeUnit.NANOSECONDS -> ChronoUnit.NANOS
      TimeUnit.MICROSECONDS -> ChronoUnit.MICROS
      TimeUnit.MILLISECONDS -> ChronoUnit.MILLIS
      TimeUnit.SECONDS -> ChronoUnit.SECONDS
      TimeUnit.MINUTES -> ChronoUnit.MINUTES
      TimeUnit.HOURS -> ChronoUnit.HOURS
      TimeUnit.DAYS -> ChronoUnit.DAYS
      else -> throw AssertionError()
   }
}
