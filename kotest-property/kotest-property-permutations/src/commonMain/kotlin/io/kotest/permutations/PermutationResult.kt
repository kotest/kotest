package io.kotest.permutations

import kotlin.time.Duration

/**
 * Models the final result of a property test.
 */
data class PermutationResult(
   val invocations: Int, // number of times we entered the check function, so includes success, failure and discards.
   val attempts: Int, // number of iterations including success, failure, but excluding discarded.
   val successes: Int, // number of iterations that were successful
   val failures: Int, // number of iterations that failed
   val discards: Int, // number of iterations that were discarded due to skipped assumptions
   val duration: Duration, // the duration of the test
   val shrinks: List<Any?>, // the shrunk values if any
   val classifications: Classifications, // the collected classifications
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
