package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.config.configuration
import kotlin.time.ExperimentalTime

/**
 * Returns the resolved timeout for a [TestCase] taking into account project settings.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.resolvedTimeout(): Long =
   this.timeout?.toLongMilliseconds() ?: configuration.timeout

/**
 * Returns the timeout for a test invocation taking into account project settings.
 */
@OptIn(ExperimentalTime::class)
fun TestCaseConfig.resolvedInvocationTimeout(): Long =
   this.invocationTimeout?.toLongMilliseconds() ?: configuration.invocationTimeout
