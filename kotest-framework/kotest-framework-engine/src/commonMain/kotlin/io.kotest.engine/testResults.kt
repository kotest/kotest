package io.kotest.engine

import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.mpp.bestName

/**
 * Returns a [TestResult] derived from a throwable.
 *
 * If the throwable is either an [AssertionError] or one of the library specific assertion types,
 * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
 */
fun Throwable.toTestResult(duration: Long): TestResult {
   return when {
      this.isFrameworkAssertionType() -> TestResult.failure(this as AssertionError, duration)
      this is AssertionError -> TestResult.failure(this, duration)
      else -> TestResult.error(this, duration)
   }
}

fun Throwable.isFrameworkAssertionType() =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
