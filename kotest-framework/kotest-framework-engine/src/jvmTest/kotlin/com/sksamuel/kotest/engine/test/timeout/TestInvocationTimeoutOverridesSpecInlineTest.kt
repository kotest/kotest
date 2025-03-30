package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tests that a test case `invocationTimeout` overrides spec level `invocationTimeout`.
 */
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestInvocationTimeoutOverridesSpecInlineTest : FunSpec() {
   init {
      extension(ExpectFailureExtension)

      invocationTimeout = 100000000

      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = 1.milliseconds,
      ) {
         delay(10.hours)
      }
   }
}
