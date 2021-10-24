package io.kotest.engine.test.logging

import io.kotest.common.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase

/**
 * An extension that is invoked when a test completes, logging any values that were logged using
 * by way of [trace], [debug], [info], [warn], and [error] during that test.
 *
 * Users can use testId on [TestCase.descriptor] to cross-reference the [TestResult] with the provided logs.
 */
@ExperimentalKotest
interface LogExtension : Extension {
   suspend fun handleLogs(testCase: TestCase, logs: List<LogEntry>)
}
