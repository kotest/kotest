package io.kotest.runner.junit.platform

import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import org.junit.platform.engine.TestExecutionResult

/**
 * Returns a JUnit [TestExecutionResult] populated from the values of the Kotest [TestResult].
 */
fun TestResult.testExecutionResult(): TestExecutionResult = when (this.status) {
   TestStatus.Ignored -> error("An ignored test cannot reach this state")
   TestStatus.Success -> TestExecutionResult.successful()
   TestStatus.Error -> TestExecutionResult.failed(this.error)
   TestStatus.Failure -> TestExecutionResult.failed(this.error)
}
