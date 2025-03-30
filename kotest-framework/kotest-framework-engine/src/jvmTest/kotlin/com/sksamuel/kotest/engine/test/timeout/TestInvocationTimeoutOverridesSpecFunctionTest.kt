package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

/**
 * Tests invocation timeouts at the spec level using inline assignment.
 */
@EnabledIf(NotMacOnGithubCondition::class)
class TestInvocationTimeoutOverridesSpecFunctionTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 1000000000
   }

   init {
      extension(ExpectFailureExtension)
      test("test case config timeout should take precedence").config(
         invocations = 3,
         invocationTimeout = 1.milliseconds,
      ) {
         delay(10.hours)
      }
   }
}
