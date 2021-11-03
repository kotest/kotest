package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import kotlin.time.Duration

class TestTimeoutOverridesSpecInlineTest : FunSpec() {
   init {
      timeout = 1
      test("test timeout should take precedence over spec inline value").config(timeout = Duration.milliseconds(1000)) {
         delay(Duration.milliseconds(50))
      }
   }
}
