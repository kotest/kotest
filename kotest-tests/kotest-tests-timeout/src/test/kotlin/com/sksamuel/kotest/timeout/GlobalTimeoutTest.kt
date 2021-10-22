package com.sksamuel.kotest.timeout

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.test.toTestResult
import kotlinx.coroutines.delay

@Isolate
class GlobalTimeoutTest : StringSpec() {

   init {

      var previousTimeout = 0L

      beforeSpec {
         previousTimeout = configuration.timeout
         configuration.timeout = 50
      }

      afterSpec {
         configuration.timeout = previousTimeout
      }

      "a global timeout should interrupt a blocked thread".config(blockingTest = true) {
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
   when (execute(testCase)) {
      is TestResult.Failure, is TestResult.Error -> TestResult.success(0)
      else -> AssertionError("${testCase.descriptor.id.value} passed but should fail").toTestResult(0)
   }
}
