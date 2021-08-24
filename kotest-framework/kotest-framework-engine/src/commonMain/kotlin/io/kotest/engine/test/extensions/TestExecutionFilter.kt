package io.kotest.engine.test.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult

internal interface TestExecutionFilter {
   suspend fun execute(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult
}
