package io.kotest.assertions.until

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * A [Interval] determines how often Kotest will invoke the predicate function for an [until] block.
 */
interface Interval {

   /**
    * Returns the next delay as a [Duration].
    *
    * @param count        The number of times the condition has been polled (evaluated) so far.
    * Always a positive integer.
    *
    * @return The duration of the next poll interval
    */
   @OptIn(ExperimentalTime::class)
   fun next(count: Int): Duration
}
