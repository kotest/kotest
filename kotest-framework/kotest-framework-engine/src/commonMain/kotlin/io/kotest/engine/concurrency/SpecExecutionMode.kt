package io.kotest.engine.concurrency

sealed class SpecExecutionMode(val concurrency: Int) {

   // All specs are executed sequentially
   // This is the default mode.
   data object Sequential : SpecExecutionMode(1)

   // All specs are executed concurrently
   // This mode is useful for running tests in parallel.
   data object Concurrent : SpecExecutionMode(Int.MAX_VALUE)

   // Specs are executed concurrently up to the given max value
   // This mode is useful for running specs in parallel but with a limit on the number of concurrent specs.
   data class LimitedConcurrency(val max: Int) : SpecExecutionMode(max)
}
