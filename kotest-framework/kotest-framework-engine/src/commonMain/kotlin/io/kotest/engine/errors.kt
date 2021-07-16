package io.kotest.engine

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
data class ProjectTimeoutException(val timeout: Long) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")

data class TestTimeoutException(val duration: Long, val testName: String) :
   Exception("Test $testName did not complete within ${duration}ms")
