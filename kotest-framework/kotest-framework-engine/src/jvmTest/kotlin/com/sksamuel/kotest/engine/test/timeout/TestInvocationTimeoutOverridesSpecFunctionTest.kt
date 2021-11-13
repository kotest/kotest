package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
class TestInvocationTimeoutOverridesSpecFunctionTest : FunSpec() {

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
