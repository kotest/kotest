package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

// tests that the values in the timeout exception are populated correctly
@EnabledIf(LinuxCondition::class)
class TestInvocationTimeoutExceptionMessageTest : FunSpec() {
   init {

      timeout = 965
      invocationTimeout = 800 // millis

      test("timeout exception should use the value that caused the test to fail 1")
         .config(invocationTimeout = 44.milliseconds) {
            delay(500.minutes)
         }

      test("timeout exception should use the value that caused the test to fail 2")
         .config(
            timeout = 454.milliseconds,
            invocationTimeout = 44.milliseconds
         ) {
            delay(500.minutes)
         }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.isErrorOrFailure shouldBe true
         result.errorOrNull?.message.shouldEndWith(" did not complete within 44ms")
         TestResult.Success(0.milliseconds)
      }
   }
}
