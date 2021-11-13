package com.sksamuel.kotest.engine.test.timeout

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.test.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

@ExperimentalKotest
class ShouldSpecTimeoutTest : ShouldSpec() {
   init {

      extension { (testCase, execute) ->
         val result = execute(testCase)
         if (testCase.name.testName.contains("timeout:") && result.isSuccess) {
            AssertionError("${testCase.descriptor.id.value} passed but should fail").toTestResult(Duration.ZERO)
         } else {
            TestResult.Success(0.milliseconds)
         }
      }

      should("timeout: root test case should timeout when duration longer than config").config(
         timeout = Duration.milliseconds(10)
      ) {
          delay(Duration.milliseconds(20))
      }

      context("timeout: container should timeout when duration longer than config").config(
         timeout = Duration.milliseconds(10)
      ) {
          delay(Duration.milliseconds(20))
      }

      context("timeout: container should timeout when nested test duration longer than container config").config(
         timeout = Duration.milliseconds(10)
      ) {
          should("timeout: a") {
              delay(Duration.milliseconds(20))
          }
      }

      context("container should allow tests to have shorter timeouts").config(timeout = Duration.minutes(1)) {

          should("timeout: nested test should override container timeouts").config(timeout = Duration.milliseconds(10)) {
              delay(Duration.milliseconds(20))
          }

          context("timeout: nested container should override container timeouts").config(
             timeout = Duration.milliseconds(10)
          ) {
              delay(Duration.milliseconds(20))
          }
      }

      context("containers should allow tests to have longer timeouts").config(timeout = Duration.milliseconds(10)) {
          should("nested test should override container timeouts").config(timeout = Duration.milliseconds(25)) {
              delay(Duration.milliseconds(20))
          }

          context("nested container should override container timeouts").config(timeout = Duration.milliseconds(25)) {
              delay(Duration.milliseconds(20))
          }
      }
   }
}
