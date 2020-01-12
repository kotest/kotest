package io.kotest.core.test

import io.kotest.Project
import io.kotest.core.test.TestCaseConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
@UseExperimental(ExperimentalTime::class)
actual fun TestCaseConfig.resolvedTimeout(): Duration = this.timeout ?: Project.timeout()
