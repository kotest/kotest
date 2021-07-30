package io.kotest.engine.test

import io.kotest.core.test.TestResult

/**
 * Creates a [TestResult] from the given error.
 * Allows for platform specific errors to be inspected.
 */
actual fun createTestResult(duration: Long, error: Throwable?): TestResult =
   defaultCreateResult(duration, error)
