package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.milliseconds

// tests that the values in the timeout exception are populated correctly
class SpecInvocationTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 897
      invocationTimeout = 10 // millis

      test("timeout exception should use the value that caused the test to fail") {
         delay(250.milliseconds)
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.error?.message shouldBe "Test did not complete within 10ms"
         TestResult.success(0)
      }
   }
}

// tests that the values in the timeout exception are populated correctly
class TestInvocationTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 965
      invocationTimeout = 800 // millis

      test("timeout exception should use the value that caused the test to fail 1").config(invocationTimeout = 44.milliseconds) {
         delay(500.milliseconds)
      }

      test("timeout exception should use the value that caused the test to fail 2").config(
         timeout = 454.milliseconds,
         invocationTimeout = 44.milliseconds
      ) {
         delay(500.milliseconds)
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         (result.status == TestStatus.Failure || result.status == TestStatus.Error) shouldBe true
         result.error?.message shouldBe "Test did not complete within 44ms"
         TestResult.success(0)
      }
   }
}


// tests that the values in the timeout exception are populated correctly
class SpecTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 21

      test("timeout exception should use the value that caused the test to fail") {
         delay(100.milliseconds)
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.error?.message shouldBe "Test did not complete within 21ms"
         TestResult.success(0)
      }
   }
}


// tests that the values in the timeout exception are populated correctly
class TestTimeoutExceptionTest : FunSpec() {
   init {

      timeout = 250

      test("timeout exception should use the value that caused the test to fail").config(timeout = 23.milliseconds) {
         delay(100.milliseconds)
      }

      aroundTest { (test, execute) ->
         val result = execute(test)
         result.error?.message shouldBe "Test did not complete within 23ms"
         TestResult.success(0)
      }
   }
}
