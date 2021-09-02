//package com.sksamuel.kotest.timeout
//
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.TestResult
//import io.kotest.core.test.TestStatus
//import io.kotest.matchers.shouldBe
//import io.kotest.matchers.string.shouldEndWith
//import kotlinx.coroutines.delay
//import kotlin.time.Duration
//
//// tests that the values in the timeout exception are populated correctly
//class SpecInvocationTimeoutExceptionTest : FunSpec() {
//   init {
//
//      timeout = 897
//      invocationTimeout = 10 // millis
//
//      test("timeout exception should use the value that caused the test to fail") {
//         delay(Duration.milliseconds(250))
//      }
//
//      aroundTest { (test, execute) ->
//         val result = execute(test)
//         result.error?.message shouldBe "Test 'timeout exception should use the value that caused the test to fail' did not complete within 10ms"
//         TestResult.success(0)
//      }
//   }
//}
//
//// tests that the values in the timeout exception are populated correctly
//class TestInvocationTimeoutExceptionTest : FunSpec() {
//   init {
//
//      timeout = 965
//      invocationTimeout = 800 // millis
//
//      test("timeout exception should use the value that caused the test to fail 1").config(
//         invocationTimeout = Duration.milliseconds(
//            44
//         )
//      ) {
//         delay(Duration.milliseconds(500))
//      }
//
//      test("timeout exception should use the value that caused the test to fail 2").config(
//         timeout = Duration.milliseconds(454),
//         invocationTimeout = Duration.milliseconds(44)
//      ) {
//         delay(Duration.milliseconds(500))
//      }
//
//      aroundTest { (test, execute) ->
//         val result = execute(test)
//         (result.status == TestStatus.Failure || result.status == TestStatus.Error) shouldBe true
//         result.error?.message.shouldEndWith(" did not complete within 44ms")
//         TestResult.success(0)
//      }
//   }
//}
//
//
//// tests that the values in the timeout exception are populated correctly
//class SpecTimeoutExceptionTest : FunSpec() {
//   init {
//
//      timeout = 21
//
//      test("timeout exception should use the value that caused the test to fail") {
//         delay(Duration.milliseconds(100))
//      }
//
//      aroundTest { (test, execute) ->
//         val result = execute(test)
//         result.error?.message shouldBe "Test 'timeout exception should use the value that caused the test to fail' did not complete within 21ms"
//         TestResult.success(0)
//      }
//   }
//}
//
//
//// tests that the values in the timeout exception are populated correctly
//class TestTimeoutExceptionTest : FunSpec() {
//   init {
//
//      timeout = 250
//
//      test("timeout exception should use the value that caused the test to fail").config(
//         timeout = Duration.milliseconds(
//            23
//         )
//      ) {
//         delay(Duration.milliseconds(100))
//      }
//
//      aroundTest { (test, execute) ->
//         val result = execute(test)
//         result.error?.message shouldBe "Test 'timeout exception should use the value that caused the test to fail' did not complete within 23ms"
//         TestResult.success(0)
//      }
//   }
//}
