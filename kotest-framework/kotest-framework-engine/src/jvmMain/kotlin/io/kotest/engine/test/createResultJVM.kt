package io.kotest.engine.test

import io.kotest.core.test.TestResult
import io.kotest.mpp.bestName

/**
 * Creates a [TestResult] from the given error.
 * Allows for platform specific errors to be inspected.
 */
internal actual fun createResult(duration: Long, error: Throwable?): TestResult = when {
   error == null -> TestResult.success(duration)
   error.isFrameworkAssertionType() -> TestResult.failure(error as AssertionError, duration)
   error is AssertionError -> TestResult.failure(error, duration)
   else -> TestResult.error(error, duration)
}

fun Throwable.isFrameworkAssertionType() =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
