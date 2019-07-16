package io.kotlintest.until

import java.time.Duration

/**
 * A [Delay] determines how often KotlinTest will invoke the predicate function for an [until] block.
 */
interface Delay {

  /**
   * Returns the next delay as a [Duration].
   *
   * @param count        The number of times the condition has been polled (evaluated) so far. Always a positive integer.
   * @return The duration of the next poll interval
   */
  fun next(count: Int): Duration
}