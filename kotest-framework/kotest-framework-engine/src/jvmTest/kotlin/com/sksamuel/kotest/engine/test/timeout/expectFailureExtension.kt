package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.TestResult
import io.kotest.engine.test.toTestResult
import kotlin.time.Duration.Companion.milliseconds

/**
 * A Test Case extension that expects each test to fail, and will invert the test result.
 */
internal val expectFailureExtension: TestCaseExtensionFn = { (testCase, execute) ->
   when (execute(testCase)) {
      is TestResult.Failure, is TestResult.Error -> TestResult.Success(0.milliseconds)
      else -> AssertionError("${testCase.descriptor.id.value} passed but should fail").toTestResult(0.milliseconds)
   }
}
