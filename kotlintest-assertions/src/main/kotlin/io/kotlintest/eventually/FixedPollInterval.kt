package io.kotlintest.eventually

import java.time.Duration

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
class FixedPollInterval(private val duration: Duration) : PollInterval {
  override fun next(count: Int): Duration {
    return duration
  }
}

fun fixedInterval(duration: Duration) = FixedPollInterval(duration)