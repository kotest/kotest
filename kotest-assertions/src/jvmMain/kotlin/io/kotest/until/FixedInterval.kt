package io.kotest.until

import java.time.Duration

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
class FixedInterval(private val duration: Duration) : Interval {
  override fun next(count: Int): Duration {
    return duration
  }
}

fun fixedInterval(duration: Duration) = FixedInterval(duration)