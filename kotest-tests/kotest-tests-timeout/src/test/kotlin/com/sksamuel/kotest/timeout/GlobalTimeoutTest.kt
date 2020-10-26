package com.sksamuel.kotest.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult
import kotlinx.coroutines.delay

class GlobalTimeoutTest : StringSpec() {

   init {

      "a global timeout should interrupt a blocked thread" {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      "a global timeout should interrupt a suspended coroutine" {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      extension(expectFailureExtension)
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
