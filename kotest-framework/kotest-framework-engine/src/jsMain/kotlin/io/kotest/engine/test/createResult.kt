package io.kotest.engine.test

import io.kotest.core.test.TestResult

actual fun createTestResult(duration: Long, error: Throwable?): TestResult =
   defaultCreateResult(duration, error)
