package io.kotlintest.core

import io.kotlintest.Project

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
actual fun TestCaseConfig.resolvedTimeout(): Long = this.timeout ?: Project.timeout()
