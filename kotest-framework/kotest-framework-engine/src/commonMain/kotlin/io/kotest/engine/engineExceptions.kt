package io.kotest.engine

import io.kotest.common.KotestInternal
import kotlin.time.Duration

/**
 * Exception used to indicate that the engine had no specs to execute.
 */
@KotestInternal
class EmptyTestSuiteException : Exception("No specs were available to test")

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
@KotestInternal
class ProjectTimeoutException(val timeout: Duration) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")
