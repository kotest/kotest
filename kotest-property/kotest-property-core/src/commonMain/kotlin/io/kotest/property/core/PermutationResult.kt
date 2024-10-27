package io.kotest.property.core

import kotlin.time.Duration

/**
 * Models the overall result of a property test.
 */
data class PermutationResult(
   val iterations: Int, // total number of iterations regardless of success or failure
   val successes: Int, // total number of iterations that were successful
   val failures: Int, // total number of iterations that failed
   val discards: Int, // total number of iterations that were discarded due to failed assumptions
   val duration: Duration, // the duration of the test
)

data class IterationResult(
   val iteration: Int, // the index of this iteration
   val success: Boolean, // whether the iteration was successful
   val successes: Int, // the total number of successful iterations so far
   val failures: Int, // the total number of failed iterations so far
   val duration: Duration, // the duration of the iteration
   val inputs: List<Any?>, // the inputs used for this iteration
   val error: Throwable?, // the particular error in case of a failure
)
