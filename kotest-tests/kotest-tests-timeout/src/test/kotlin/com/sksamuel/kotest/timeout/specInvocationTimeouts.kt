//package com.sksamuel.kotest.timeout
//
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.spec.style.funSpec
//import kotlinx.coroutines.delay
//import kotlin.time.Duration
// todo restore tests
//private val factory = funSpec {
//   test("long running test") {
//      delay(Duration.hours(10))
//   }
//}
//
///**
// * Tests invocation timeouts at the spec level using inline assignment.
// */
//class SpecInlineInvocationTimeoutTest : FunSpec() {
//   init {
//      extension(expectFailureExtension)
//
//      invocationTimeout = 50
//
//      test("should take timeout from spec setting").config(invocations = 3) {
//         delay(Duration.seconds(1))
//      }
//
//      // should apply to factories too
//      include(factory)
//   }
//}
//
///**
// * Tests invocation timeouts at the spec level using inline assignment.
// */
//class FunctionOverrideInvocationPrecedenceTimeoutTest : FunSpec() {
//
//   override fun invocationTimeout(): Long {
//      return 1000000000
//   }
//
//   init {
//      extension(expectFailureExtension)
//      test("test case config timeout should take precedence").config(
//         invocations = 3,
//         invocationTimeout = Duration.milliseconds(150),
//      ) {
//         delay(Duration.hours(10))
//      }
//   }
//}
//
///**
// * Tests that a test case invocation timeout overrides spec level inline assignment.
// */
//class InlineInvocationPrecedenceTimeoutTest : FunSpec() {
//   init {
//      extension(expectFailureExtension)
//
//      invocationTimeout = 100000000
//
//      test("test case config timeout should take precedence").config(
//         invocations = 3,
//         invocationTimeout = Duration.milliseconds(150),
//      ) {
//         delay(Duration.hours(10))
//      }
//   }
//}
//
///**
// * Tests that a test case invocation timeout overrides spec level.
// */
//class FunctionOverrideInvocationTimeoutTest : FunSpec() {
//
//   override fun invocationTimeout(): Long {
//      return 150
//   }
//
//   init {
//      extension(expectFailureExtension)
//      test("should take timeout from spec setting").config(invocations = 3) {
//         delay(Duration.hours(10))
//      }
//
//      // should apply to factories too
//      include(factory)
//   }
//}
