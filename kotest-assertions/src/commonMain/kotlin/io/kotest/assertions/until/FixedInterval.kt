package io.kotest.assertions.until

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
@UseExperimental(ExperimentalTime::class)
class FixedInterval(private val duration: Duration) : Interval {
   override fun next(count: Int): Duration {
      return duration
   }
}

@UseExperimental(ExperimentalTime::class)
@Deprecated("use duration.fibonacci()")
fun fixedInterval(duration: Duration) = FixedInterval(duration)

@UseExperimental(ExperimentalTime::class)
fun Duration.fixed() = FixedInterval(this)
