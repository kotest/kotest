package io.kotest.engine

/**
 * Exception used for when a test exceeds its timeout.
 */
class TestTimeoutException(val timeout: Long, val testName: String) :
   Exception("Test '$testName' did not complete within ${timeout}ms")
