package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours

private val factory = funSpec {
   test("long running test") {
      delay(10.hours)
   }
}

/**
 * Tests `invocationTimeout` at the spec level using inline assignment.
 */
@EnabledIf(LinuxCondition::class)
class SpecInlineInvocationTimeoutTest : FunSpec() {
   init {
      extension(ExpectFailureExtension)

      invocationTimeout = 1

      test("should take timeout from spec setting").config(invocations = 3) {
         delay(1.hours)
      }

      // should apply to factories too
      include(factory)
   }
}
