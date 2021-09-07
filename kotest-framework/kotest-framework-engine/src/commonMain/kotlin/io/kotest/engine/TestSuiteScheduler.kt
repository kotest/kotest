package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.listener.TestEngineListener

/**
 * A [TestSuiteScheduler] is responsible for launching each spec from a [TestSuite] into a coroutine.
 */
@ExperimentalKotest
interface TestSuiteScheduler {
   suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult
}

/**
 * Returns the appropriate TestSuiteScheduler for the platform.
 */
expect fun createTestSuiteScheduler(): TestSuiteScheduler
