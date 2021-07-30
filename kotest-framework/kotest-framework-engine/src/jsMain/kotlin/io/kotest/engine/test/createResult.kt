package io.kotest.engine.test

import io.kotest.core.test.TestResult

internal actual fun createResult(duration: Long, error: Throwable?): TestResult =
   defaultCreateResult(duration, error)
