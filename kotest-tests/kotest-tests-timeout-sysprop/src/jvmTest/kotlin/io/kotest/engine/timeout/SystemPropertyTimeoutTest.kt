package io.kotest.engine.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.toTestResult
import kotlinx.coroutines.delay

class SystemPropertyTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)
      context("using system property timeouts") {
         test("this test should timeout due to the test timeout set via sys props").config(invocations = 100) {
            delay(10)
         }
         test("this test should timeout due to the invocation timeout set via sys props") {
            delay(1000)
         }
      }
   }
}

/**
 * A Test Case extension that expects each test to fail, and will invert the test result.
 */
val expectFailureExtension: TestCaseExtensionFn = { (testCase, execute) ->
   val result = execute(testCase)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(0)
      else -> AssertionError("${testCase.descriptor.id.value} passed but should fail").toTestResult(0)
   }
}
