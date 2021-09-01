package io.kotest.engine.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.spec.TestSuiteScheduler

/**
 * An extension point that allows for custom implementations of [TestSuiteScheduler].
 */
@ExperimentalKotest
interface TestSuiteSchedulerExtension {
   fun scheduler(): TestSuiteScheduler
}
