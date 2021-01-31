package io.kotest.assertions.until

import kotlin.time.Duration

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
class FixedInterval(private val duration: Duration) : Interval {
   override fun toString() = "FixedInterval(${::duration.name}=$duration)"

   override fun next(count: Int): Duration {
      return duration
   }
}

fun Duration.fixed(): FixedInterval = FixedInterval(this)
