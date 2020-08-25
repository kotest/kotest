package com.sksamuel.kotest.engine.spec.timeouts

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
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
 * Tests invoation timeouts at the spec level using inline assignment.
 */
@OptIn(ExperimentalTime::class)
class InlineInvocationTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      invocationTimeout = 150

      test("should take timeout from spec setting").config(invocations = 3) {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}

/**
 * Tests invoation timeouts at the spec level using inline assignment.
 */
@OptIn(ExperimentalTime::class)
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
 * Tests invoation timeouts at the spec level using function override.
 */
@OptIn(ExperimentalTime::class)
class FunctionOverrideInvocationTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long? {
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

/**
 * Tests invoation timeouts at the spec level using function override.
 */
@OptIn(ExperimentalTime::class)
class FunctionOverrideInvocationPrecedenceTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long? {
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
