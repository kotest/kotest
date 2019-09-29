package io.kotest.until

import java.time.Duration

/**
 * A [Interval] determines how often Kotest will invoke the predicate function for an [until] block.
 */
interface Interval {

  /**
   * Returns the next delay as a [Duration].
   *
   * @param count        The number of times the condition has been polled (evaluated) so far. Always a positive integer.
   * @return The duration of the next poll interval
   */
  fun next(count: Int): Duration
}