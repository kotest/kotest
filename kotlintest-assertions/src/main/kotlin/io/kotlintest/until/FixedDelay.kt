package io.kotlintest.until

import java.time.Duration

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
class FixedDelay(private val duration: Duration) : Delay {
  override fun next(count: Int): Duration {
    return duration
  }
}

fun fixedDelay(duration: Duration) = FixedDelay(duration)