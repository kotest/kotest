package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.engine.TestSuite
import io.kotest.engine.extensions.TestSuiteSchedulerExtension
import kotlin.reflect.KClass

/**
 * A [TestSuiteScheduler] is responsible for launching the specs from a [TestSuite] into coroutines.
 *
 * Register a [TestSuiteSchedulerExtension] to provide a custom implementation.
 */
@ExperimentalKotest
interface TestSuiteScheduler {
   suspend fun schedule(suite: TestSuite, f1: (Spec) -> Unit, f2: (KClass<out Spec>) -> Unit)
}
