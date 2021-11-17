package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tests that the timeout in a test case should take precedence over the timeout at a spec level.
 */
class TestTimeoutOverridesSpecFunctionTest : FunSpec() {

   override fun timeout(): Long = 1.milliseconds.inWholeMilliseconds

   init {
      test("test case config timeout should take precedence").config(timeout = 250.milliseconds) {
         // this test should pass because 50 < 250, and 250 should override the 1 at the spec level
         delay(50.milliseconds)
      }
   }
}
