package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

// tests that the values in the timeout exception are populated correctly
class TestInvocationTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 965
      invocationTimeout = 800 // millis

      test("timeout exception should use the value that caused the test to fail 1")
         .config(invocationTimeout = 44.milliseconds) {
            delay(Duration.minutes(500))
         }

      test("timeout exception should use the value that caused the test to fail 2")
         .config(
            timeout = Duration.milliseconds(454),
            invocationTimeout = Duration.milliseconds(44)
         ) {
            delay(Duration.minutes(500))
         }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.isErrorOrFailure shouldBe true
         result.errorOrNull?.message.shouldEndWith(" did not complete within 44ms")
         TestResult.Success(0.milliseconds)
      }
   }
}
