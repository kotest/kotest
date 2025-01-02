package io.kotest.engine.concurrency

sealed class TestExecutionMode(val concurrency: Int) {

   // All tests are executed sequentially
   // This is the default mode.
   data object Sequential : TestExecutionMode(1)

   // All tests are executed concurrently
   // This mode is useful for running tests in parallel.
   data object Concurrent : TestExecutionMode(Int.MAX_VALUE)

   // Tests are executed concurrently up to the given max value
   // This mode is useful for running tests in parallel but with a limit on the number of concurrent tests.
   data class LimitedConcurrency(val max: Int) : TestExecutionMode(max)
}

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
