package com.sksamuel.kotest.engine.spec.timeout

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.test.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.Duration

@ExperimentalKotest
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

      test("timeout: root test case should timeout when duration longer than config").config(
          timeout = Duration.milliseconds(
              10
          )
      ) {
          delay(Duration.milliseconds(20))
      }

      context("timeout: container should timeout when duration longer than config").config(
          timeout = Duration.milliseconds(
              10
          )
      ) {
          delay(Duration.milliseconds(20))
      }

      context("timeout: container should timeout when nested test duration longer than container config").config(
          timeout = Duration.milliseconds(
              10
          )
      ) {
          test("timeout: a") {
              delay(Duration.milliseconds(20))
          }
      }

      context("container should allow tests to have shorter timeouts").config(timeout = Duration.minutes(1)) {

         test("timeout: nested test should override container timeouts").config(timeout = Duration.milliseconds(10)) {
            delay(Duration.milliseconds(20))
         }

         context("timeout: nested container should override container timeouts").config(
            timeout = Duration.milliseconds(
               10
            )
         ) {
            delay(Duration.milliseconds(20))
         }
      }

      context("containers should allow tests to have longer timeouts").config(timeout = Duration.milliseconds(10)) {
          test("nested test should override container timeouts").config(timeout = Duration.milliseconds(25)) {
              delay(Duration.milliseconds(20))
          }

          context("nested container should override container timeouts").config(timeout = Duration.milliseconds(25)) {
              delay(Duration.milliseconds(20))
          }
      }
   }
}
