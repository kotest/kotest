package io.kotest.permutations

import kotlin.time.Duration

/**
 * Models the overall result of a property test.
 */
data class PermutationResult(
   val attempts: Int, // total number of iterations regardless of success or failure
   val successes: Int, // total number of iterations that were successful
   val failures: Int, // total number of iterations that failed
   val discards: Int, // total number of iterations that were discarded due to failed assumptions
   val duration: Duration, // the duration of the test
   val shrinks: List<Any?>, // the shrunk values
)

data class IterationFailure(
   val iteration: Int, // the index of this iteration
   val success: Boolean, // whether the iteration was successful
   val successes: Int, // the total number of successful iterations so far
   val failures: Int, // the total number of failed iterations so far
   val duration: Duration, // the duration of the iteration
   val inputs: List<Input>, // the inputs used for this iteration
   val error: Throwable, // the particular error in case of a failure
)

data class Input(val name: String?, val value: Any?)
