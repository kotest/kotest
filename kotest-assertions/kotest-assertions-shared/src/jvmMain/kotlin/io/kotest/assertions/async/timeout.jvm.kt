package io.kotest.assertions.async

import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.toKotlinDuration

/**
 * Assert [thunk] does not complete within [duration].
 */
suspend fun <A> shouldTimeout(duration: Duration, thunk: suspend () -> A): Unit =
   shouldTimeout(duration.toKotlinDuration()) { thunk() }

/**
 * Assert [thunk] does not complete within [timeout]/[unit].
 */
suspend fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: suspend () -> A) {
   shouldTimeout(Duration.of(timeout, unit.toChronoUnit())) { thunk() }
}
