@file:Suppress("DEPRECATION") // FIXME remove deprecation suppression when io.kotest.assertions.until.Interval is removed

package io.kotest.assertions.until

import kotlin.time.Duration

/**
 * Generates a fixed (linear) poll interval based on the supplied duration
 */
@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class FixedInterval(private val duration: Duration) : Interval {
   override fun toString() = "FixedInterval(${::duration.name}=$duration)"

   override fun next(count: Int): Duration {
      return duration
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
fun Duration.fixed(): FixedInterval = FixedInterval(this)
