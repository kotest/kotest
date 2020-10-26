package io.kotest.assertions.until

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
@OptIn(ExperimentalTime::class)
class FixedInterval(private val duration: Duration) : Interval {
   override fun next(count: Int): Duration {
      return duration
   }
}

@OptIn(ExperimentalTime::class)
fun Duration.fixed() = FixedInterval(this)
