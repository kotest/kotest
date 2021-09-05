package io.kotest.engine

import io.kotest.core.config.configuration

/**
 * Returns the appropriate TestSuiteScheduler for the platform.
 */
actual fun createTestSuiteScheduler(): TestSuiteScheduler {
   return ConcurrentTestSuiteScheduler(configuration.concurrentSpecs ?: configuration.parallelism)
}
