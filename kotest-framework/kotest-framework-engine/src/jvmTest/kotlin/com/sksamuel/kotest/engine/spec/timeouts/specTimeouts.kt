package com.sksamuel.kotest.engine.spec.timeouts

import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.toTestResult
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
private val factory = funSpec {
   test("long running test") {
      delay(10.hours)
   }
}

/**
 * Tests timeouts at the spec level using inline assignment.
 */
@OptIn(ExperimentalTime::class)
class InlineTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      timeout = 250

      test("should timeout from spec setting") {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}

@OptIn(ExperimentalTime::class)
class InlineTimeoutPrecenceTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      timeout = 10000000000

      test("test case config timeout should take precedence").config(timeout = 250.milliseconds) {
         delay(10.hours)
      }
   }
}

/**
 * Tests timeouts at the spec level using function override.
 */
@OptIn(ExperimentalTime::class)
class OverrideTimeoutTest : FunSpec() {

   override fun timeout(): Long = 250

   init {
      extension(expectFailureExtension)

      test("should timeout from spec setting") {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}

@OptIn(ExperimentalTime::class)
class OverrideTimeoutPrecenceTest : FunSpec() {

   override fun timeout(): Long = 10000000000

   init {
      extension(expectFailureExtension)

      test("test case config timeout should take precedence").config(timeout = 250.milliseconds) {
         delay(10.hours)
      }
   }
}

/**
 * A Test Case extension that expects each test to fail, and will invert the test result.
 */
val expectFailureExtension: TestCaseExtensionFn = { (testCase, execute) ->
   val result = execute(testCase)
   when (result.status) {
      TestStatus.Failure, TestStatus.Error -> TestResult.success(0)
      else -> AssertionError("${testCase.description.name.name} passed but should fail").toTestResult(0)
   }
}
