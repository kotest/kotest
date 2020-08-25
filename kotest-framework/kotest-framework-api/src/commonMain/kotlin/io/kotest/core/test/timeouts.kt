package io.kotest.core.test

import io.kotest.core.config.configuration
import kotlin.time.ExperimentalTime

/**
 * Returns the resolved timeout for a [TestCase] taking into account test case config,
 * spec overrides, and project defaults.
 */
@OptIn(ExperimentalTime::class)
fun TestCase.resolvedTimeout(): Long =
   config.timeout?.toLongMilliseconds() ?: spec.timeout ?: spec.timeout() ?: configuration.timeout

/**
 * Returns the resolved timeout for a test invocation taking into account test case config,
 * spec overrides, and project defaults.
 */
@OptIn(ExperimentalTime::class)
fun TestCase.resolvedInvocationTimeout(): Long =
   config.invocationTimeout?.toLongMilliseconds() ?: spec.invocationTimeout() ?: spec.invocationTimeout
   ?: configuration.invocationTimeout
