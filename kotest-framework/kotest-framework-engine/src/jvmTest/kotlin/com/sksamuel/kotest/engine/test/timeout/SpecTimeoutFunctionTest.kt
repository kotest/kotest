package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

private val factory = funSpec {
   test("long running test") {
      delay(10.hours)
   }
}

/**
 * Tests timeouts at the spec level (by function override) should be applied.
 */
class SpecTimeoutFunctionTest : FunSpec() {

   override fun timeout(): Long = 10.milliseconds.inWholeMilliseconds

   init {
      extension(ExpectFailureExtension)

      test("should timeout from spec setting") {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}
