package io.kotest.engine.test

import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlin.jvm.JvmName

/**
 * Returns a [TestResult] derived from a throwable.
 *
 * If the throwable is null, then a succesfull result is returned.
 *
 * If the throwable is either an [AssertionError] or one of the platform specific assertion types,
 * then a [TestStatus.Failure] will be returned, otherwise a [TestStatus.Error] will be returned.
 */
internal expect fun createResult(duration: Long, error: Throwable?): TestResult

@JvmName("throwableToTestResult")
fun Throwable.toTestResult(duration: Long): TestResult = createResult(duration, this)

internal fun defaultCreateResult(duration: Long, error: Throwable?) = when (error) {
   null -> TestResult.success(duration)
   is AssertionError -> TestResult.failure(error, duration)
   else -> TestResult.error(error, duration)
}

