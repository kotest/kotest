package io.kotest.engine.test

import io.kotest.core.test.TestResult
import io.kotest.mpp.bestName
import kotlin.jvm.JvmName
import kotlin.time.Duration

/**
 * Returns a [TestResult] derived from a throwable.
 *
 * If the throwable is null, then a succesfull result is returned.
 *
 * If the throwable is either an [AssertionError] or one of the platform specific assertion types,
 * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
 */
@Deprecated(
   "Replaced with createTestResult(Duration, Throwable?)",
   ReplaceWith("createTestResult(duration.milliseconds, error)")
)
fun createTestResult(duration: Long, error: Throwable?): TestResult =
   createTestResult(Duration.milliseconds(duration), error)

fun createTestResult(duration: Duration, error: Throwable?): TestResult = when {
   error == null -> TestResult.Success(duration)
   error.isFrameworkAssertionType() -> TestResult.Failure(duration, error as AssertionError)
   error is AssertionError -> TestResult.Failure(duration, error)
   else -> TestResult.Error(duration, error)
}

@JvmName("throwableToTestResultLong")
@Deprecated(
   "Replaced with Throwable.toTestResult(Duration)",
   ReplaceWith("createTestResult(duration.milliseconds, this)", "kotlin.time.milliseconds")
)
fun Throwable.toTestResult(duration: Long): TestResult = createTestResult(Duration.milliseconds(duration), this)

@JvmName("throwableToTestResult")
fun Throwable.toTestResult(duration: Duration): TestResult = createTestResult(duration, this)

fun Throwable.isFrameworkAssertionType() =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
