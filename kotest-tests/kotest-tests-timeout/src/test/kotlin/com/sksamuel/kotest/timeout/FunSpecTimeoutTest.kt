package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlinx.coroutines.delay

class FunSpecTimeoutTest : FunSpec() {
   init {

      extension { (testCase, execute) ->
         val result = execute(testCase)
         if (testCase.displayName.contains("timeout:") && result.status == TestStatus.Success) {
            AssertionError("${testCase.description.name.name} passed but should fail").toTestResult(0)
         } else {
            TestResult.success(0)
         }
      }

      test("timeout: root test case should timeout when duration longer than config").config(timeout = 10.milliseconds) {
         delay(20.milliseconds)
      }

      context("timeout: container should timeout when duration longer than config").config(timeout = 10.milliseconds) {
         delay(20.milliseconds)
      }

      context("timeout: container should timeout when nested test duration longer than container config").config(timeout = 10.milliseconds) {
         test("timeout: a") {
            delay(20.milliseconds)
         }
      }

      context("container should allow tests to have shorter timeouts").config(timeout = 1.minutes) {

         test("timeout: nested test should override container timeouts").config(timeout = 10.milliseconds) {
            delay(20.milliseconds)
         }

         context("timeout: nested container should override container timeouts").config(timeout = 10.milliseconds) {
            delay(20.milliseconds)
         }
      }

      context("containers should allow tests to have longer timeouts").config(timeout = 10.milliseconds) {
         test("nested test should override container timeouts").config(timeout = 25.milliseconds) {
            delay(20.milliseconds)
         }

         context("nested container should override container timeouts").config(timeout = 25.milliseconds) {
            delay(20.milliseconds)
         }
      }
   }
}
