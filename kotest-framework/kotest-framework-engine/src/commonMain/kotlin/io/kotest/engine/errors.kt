package io.kotest.engine

/**
 * Exception thrown if the overall project/test suite takes longer than a specified timeout.
 */
class ProjectTimeoutException(val timeout: Long) :
   Exception("Test suite did not complete with ${timeout / 1000} seconds")

/**
 * Exception used for when a test exceeds its timeout.
 */
class TestTimeoutException(val timeout: Long, val testName: String) :
   Exception("Test '$testName' did not complete within ${timeout}ms")
