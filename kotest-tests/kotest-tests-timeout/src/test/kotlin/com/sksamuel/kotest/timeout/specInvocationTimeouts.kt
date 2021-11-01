package com.sksamuel.kotest.timeout

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.test.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

private val factory = funSpec {
   test("long running test") {
      delay(Duration.hours(10))
   }
}

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
class SpecInlineInvocationTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      invocationTimeout = 1

      test("should take timeout from spec setting").config(invocations = 3) {
         delay(Duration.hours(1))
      }

      // should apply to factories too
      include(factory)
   }
}

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
class FunctionOverrideInvocationPrecedenceTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 1000000000
   }

   init {
      extension(expectFailureExtension)
      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = Duration.milliseconds(1),
      ) {
         delay(Duration.hours(10))
      }
   }
}

/**
 * Tests that a test case invocation timeout overrides spec level inline assignment.
 */
class InlineInvocationPrecedenceTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      invocationTimeout = 100000000

      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = Duration.milliseconds(1),
      ) {
         delay(Duration.hours(10))
      }
   }
}

/**
 * Tests that a test case invocation timeout overrides spec level.
 */
class FunctionOverrideInvocationTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 10
   }

   init {
      extension(expectFailureExtension)
      test("should take timeout from spec setting").config(invocations = 3) {
         delay(Duration.hours(10))
      }

      // should apply to factories too
      include(factory)
   }
}



/**
 * A Test Case extension that expects each test to fail, and will invert the test result.
 */
val expectFailureExtension: TestCaseExtensionFn = { (testCase, execute) ->
   when (execute(testCase)) {
      is TestResult.Failure, is TestResult.Error -> TestResult.Success(0.milliseconds)
      else -> AssertionError("${testCase.descriptor.id.value} passed but should fail").toTestResult(0.milliseconds)
   }
}
