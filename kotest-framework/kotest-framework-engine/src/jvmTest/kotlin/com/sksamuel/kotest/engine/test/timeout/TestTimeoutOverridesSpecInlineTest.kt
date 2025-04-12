package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestTimeoutOverridesSpecInlineTest : FunSpec() {
   init {
      timeout = 1
      test("test timeout should take precedence over spec inline value").config(timeout = 1000.milliseconds) {
         delay(50.milliseconds)
      }
   }
}
