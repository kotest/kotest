package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration

/**
 * Tests that a test case `invocationTimeout` overrides spec level `invocationTimeout`.
 */
class TestInvocationTimeoutOverridesSpecInlineTest : FunSpec() {
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
