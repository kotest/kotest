package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class TestTimeoutOverridesSpecInlineTest : FunSpec() {
   init {
      timeout = 1
      test("test timeout should take precedence over spec inline value").config(timeout = 1000.milliseconds) {
         delay(50.milliseconds)
      }
   }
}
