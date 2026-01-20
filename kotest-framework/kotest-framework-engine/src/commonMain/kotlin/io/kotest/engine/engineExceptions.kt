package io.kotest.engine

import kotlin.time.Duration

/**
 * Exception used to indicate that the engine had no specs to execute.
 */
class EmptyTestSuiteException : Exception("No specs were available to test")

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
class ProjectTimeoutException(val timeout: Duration) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")
