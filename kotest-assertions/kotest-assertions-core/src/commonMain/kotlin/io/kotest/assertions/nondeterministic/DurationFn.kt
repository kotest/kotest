package io.kotest.assertions.nondeterministic

import kotlin.time.Duration

/**
 * A [DurationFn] calculates a duration based on an input counter.
 */
fun interface DurationFn {

   /**
    * Returns the next duration.
    *
    * @param count The number of times the condition has been polled (evaluated) so far. Always a positive integer.
    */
   fun next(count: Int): Duration
}
