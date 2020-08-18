package io.kotest.engine

import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.mpp.bestName
import kotlin.jvm.JvmName

/**
 * Returns a [TestResult] derived from a throwable.
 *
 * If the throwable is either an [AssertionError] or one of the library specific assertion types,
 * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
 */
fun toTestResult(t: Throwable?, duration: Long): TestResult {
   return when {
      t == null -> TestResult.success(duration)
      t.isFrameworkAssertionType() -> TestResult.failure(t as AssertionError, duration)
      t is AssertionError -> TestResult.failure(t, duration)
      else -> TestResult.error(t, duration)
   }
}

@JvmName("throwableToTestResult")
fun Throwable.toTestResult(duration: Long): TestResult = toTestResult(this, duration)

fun Throwable.isFrameworkAssertionType() =
   listOf(
      "org.opentest4j.AssertionFailedError",
      "AssertionFailedError",
      "org.junit.ComparisonFailure",
      "ComparisonFailure"
   ).contains(this::class.bestName())
