package io.kotest.runner.junit.platform

import io.kotest.engine.test.TestResult
import org.junit.platform.engine.TestExecutionResult

/**
 * Returns a JUnit [org.junit.platform.engine.TestExecutionResult] populated from the values of the Kotest [io.kotest.engine.test.TestResult].
 */
internal fun TestResult.toTestExecutionResult(): TestExecutionResult = when (this) {
   is TestResult.Ignored -> error("An ignored test cannot reach this state")
   is TestResult.Success -> TestExecutionResult.successful()
   is TestResult.Error -> TestExecutionResult.failed(this.errorOrNull)
   is TestResult.Failure -> TestExecutionResult.failed(this.errorOrNull)
}
