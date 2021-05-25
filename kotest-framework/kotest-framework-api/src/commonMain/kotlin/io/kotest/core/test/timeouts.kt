package io.kotest.core.test

import io.kotest.core.config.configuration

/**
 * Returns the resolved timeout for a [TestCase] taking into account test case config,
 * spec overrides, and project defaults.
 */
fun TestCase.resolvedTimeout(): Long =
   config.timeout?.inWholeMilliseconds ?: spec.timeout ?: spec.timeout() ?: configuration.timeout

/**
 * Returns the resolved timeout for a test invocation taking into account config on the test case,
 * values specified in the spec itself, and project wide defaults.
 */
fun TestCase.resolvedInvocationTimeout(): Long =
   config.invocationTimeout?.inWholeMilliseconds ?: spec.invocationTimeout() ?: spec.invocationTimeout
   ?: configuration.invocationTimeout
