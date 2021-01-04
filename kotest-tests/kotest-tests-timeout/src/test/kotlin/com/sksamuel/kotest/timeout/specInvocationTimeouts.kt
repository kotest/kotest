package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
private val factory = funSpec {
   test("long running test") {
      delay(10.hours)
   }
}

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
@ExperimentalTime
class SpecInlineInvocationTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      invocationTimeout = 50

      test("should take timeout from spec setting").config(invocations = 3) {
         delay(1.seconds)
      }

      // should apply to factories too
      include(factory)
   }
}

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
@ExperimentalTime
class FunctionOverrideInvocationPrecedenceTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 1000000000
   }

   init {
      extension(expectFailureExtension)
      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = 150.milliseconds,
      ) {
         delay(10.hours)
      }
   }
}

/**
 * Tests that a test case invocation timeout overrides spec level inline assignment.
 */
@ExperimentalTime
class InlineInvocationPrecedenceTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      invocationTimeout = 100000000

      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = 150.milliseconds,
      ) {
         delay(10.hours)
      }
   }
}

/**
 * Tests that a test case invocation timeout overrides spec level.
 */
@ExperimentalTime
class FunctionOverrideInvocationTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 150
   }

   init {
      extension(expectFailureExtension)
      test("should take timeout from spec setting").config(invocations = 3) {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}
