package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.milliseconds
import kotlin.time.minutes

@OptIn(ExperimentalTime::class)
private val factory = funSpec {
   test("long running test") {
      delay(10.hours)
   }
}

/**
 * Tests timeouts at the spec level using inline assignment should be applied.
 */
@OptIn(ExperimentalTime::class)
class InlineTimeoutTest : FunSpec() {
   init {
      extension(expectFailureExtension)

      timeout = 10.milliseconds.toLongMilliseconds()

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
 * Tests timeouts at the spec level (by function override) should be applied.
 */
@OptIn(ExperimentalTime::class)
class OverrideTimeoutTest : FunSpec() {

   override fun timeout(): Long = 10.milliseconds.toLongMilliseconds()

   init {
      extension(expectFailureExtension)

      test("should timeout from spec setting") {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}

/**
 * Tests that the timeout in a test case should take precedence over the timeout at a spec level.
 */
@OptIn(ExperimentalTime::class)
class OverrideTimeoutPrecenceTest : FunSpec() {

   override fun timeout(): Long = 1.hours.toLongMilliseconds()

   init {
      extension(expectFailureExtension)

      test("test case config timeout should take precedence").config(timeout = 250.milliseconds) {
         delay(2.minutes)
      }
   }
}
