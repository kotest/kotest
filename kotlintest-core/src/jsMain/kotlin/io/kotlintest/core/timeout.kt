package io.kotlintest.core

/**
 * Returns the timeout for a [TestCase] taking into account global settings.
 */
actual fun TestCaseConfig.resolvedTimeout(): Long = this.timeout ?: 600
