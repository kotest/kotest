package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration

private val factory = funSpec {
   test("long running test") {
      delay(Duration.hours(10))
   }
}

/**
 * Tests timeouts at the spec level using inline assignment should be applied.
 */
class InlineTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      timeout = Duration.milliseconds(10).inWholeMilliseconds

      test("should timeout from spec setting") {
         delay(Duration.hours(10))
      }

      // should apply to factories too
      include(factory)
   }
}

class InlineTimeoutFailurePrecedenceTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      timeout = 10000000000

      test("test case config timeout should take precedence").config(timeout = Duration.milliseconds(1)) {
         delay(Duration.hours(10))
      }
   }
}

class InlineTimeoutSuccessPrecedenceTest : FunSpec() {
   init {
      timeout = 1
      test("test case config timeout should take precedence").config(timeout = Duration.milliseconds(50)) {
         // this test should pass we increased the timeout to 50, which is greater than the 10 delay here
         // if the config timeout did not have precendence over the spec timeout, we would hit the 1ms timeout
         delay(Duration.milliseconds(20))
      }
   }
}

/**
 * Tests timeouts at the spec level (by function override) should be applied.
 */
class OverrideTimeoutTest : FunSpec() {

   override fun timeout(): Long = Duration.milliseconds(10).inWholeMilliseconds

   init {
      extension(expectFailureExtension)

      test("should timeout from spec setting") {
         delay(Duration.hours(10))
      }

      // should apply to factories too
      include(factory)
   }
}

/**
 * Tests that the timeout in a test case should take precedence over the timeout at a spec level.
 */
class OverrideTimeoutFailurePrecenceTest : FunSpec() {

   override fun timeout(): Long = Duration.hours(1).inWholeMilliseconds

   init {
      extension(expectFailureExtension)

      test("test case config timeout should take precedence").config(timeout = Duration.milliseconds(250)) {
         delay(Duration.minutes(2))
      }
   }
}

/**
 * Tests that the timeout in a test case should take precedence over the timeout at a spec level.
 */
class OverrideTimeoutSuccessPrecenceTest : FunSpec() {

   override fun timeout(): Long = Duration.milliseconds(1).inWholeMilliseconds

   init {
      test("test case config timeout should take precedence").config(timeout = Duration.milliseconds(250)) {
         // this test should pass because 50 < 250, and 250 should override the 1 at the spec level
         delay(Duration.milliseconds(50))
      }
   }
}
