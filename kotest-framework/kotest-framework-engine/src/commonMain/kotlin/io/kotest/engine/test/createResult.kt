package io.kotest.engine.test

import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.mpp.bestName
import kotlin.jvm.JvmName

/**
 * Returns a [TestResult] derived from a throwable.
 *
 * If the throwable is null, then a succesfull result is returned.
 *
 * If the throwable is either an [AssertionError] or one of the platform specific assertion types,
 * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
 */
fun createTestResult(duration: Long, error: Throwable?): TestResult = when {
   error == null -> TestResult.success(duration)
   error.isFrameworkAssertionType() -> TestResult.failure(error as AssertionError, duration)
   error is AssertionError -> TestResult.failure(error, duration)
   else -> TestResult.error(error, duration)
}

@JvmName("throwableToTestResult")
fun Throwable.toTestResult(duration: Long): TestResult = createTestResult(duration, this)

fun Throwable.isFrameworkAssertionType() =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
