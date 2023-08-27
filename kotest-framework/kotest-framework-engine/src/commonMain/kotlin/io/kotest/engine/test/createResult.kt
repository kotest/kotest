package io.kotest.engine.test

import io.kotest.core.test.TestResult
import io.kotest.mpp.bestName
import kotlin.time.Duration

/**
 * Creates a [TestResult] which is a [TestResult.success] if the supplied exception is null,
 * a [TestResult.failure] if the supplied error is an assertion failure, or a [TestResult.error]
 * if the exception is any other type.
 */
fun createTestResult(duration: Duration, error: Throwable?): TestResult = when {
   error == null -> TestResult.Success(duration)
   error.isFrameworkAssertionType() -> TestResult.Failure(duration, error as AssertionError)
   error is AssertionError -> TestResult.Failure(duration, error)
   else -> TestResult.Error(duration, error)
}

fun Throwable.toTestResult(duration: Duration): TestResult = createTestResult(duration, this)

/**
 * Returns true if the receiver is one of the supported assertion failure exception types.
 */
internal fun Throwable.isFrameworkAssertionType(): Boolean =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
