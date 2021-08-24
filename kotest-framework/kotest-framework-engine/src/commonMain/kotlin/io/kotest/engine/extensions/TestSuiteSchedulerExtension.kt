package io.kotest.engine.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.spec.TestSuiteScheduler

@ExperimentalKotest
interface TestSuiteSchedulerExtension {
   fun scheduler(): TestSuiteScheduler
}
