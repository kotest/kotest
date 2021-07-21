package io.kotest.engine.test

import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase

/**
 * Returns the resolved timeout for a [TestCase] in milliseconds.
 *
 * The value cascades from the following settings in order:
 * - test case config
 * - spec
 * - project default
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
