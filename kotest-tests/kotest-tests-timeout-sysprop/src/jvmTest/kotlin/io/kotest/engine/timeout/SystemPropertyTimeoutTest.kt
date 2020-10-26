package io.kotest.engine.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SystemPropertyTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)
      context("using system property timeouts") {
         test("this test should timeout due to 2000 test timeout").config(invocations = 6) {
            delay(500)
         }
         test("this test should timeout due to 1000 invocation timeout").config(invocations = 1) {
            delay(1500)
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
      else -> AssertionError("${testCase.description.name.name} passed but should fail").toTestResult(0)
   }
}
