package io.kotest.core

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
@UseExperimental(ExperimentalTime::class)
actual fun TestCaseConfig.resolvedTimeout(): Duration = this.timeout ?: 600.seconds
