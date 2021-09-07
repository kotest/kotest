package io.kotest.engine

/**
 * Returns the appropriate TestSuiteScheduler for the platform.
 */
actual fun createTestSuiteScheduler(): TestSuiteScheduler {
   return ConcurrentTestSuiteScheduler(1)
}
