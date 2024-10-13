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

@EnabledIf(LinuxCondition::class)
class FunctionOverrideInvocationTimeoutTest : FunSpec() {

   override fun invocationTimeout(): Long {
      return 12
   }

   init {
      extension(ExpectFailureExtension)
      test("should take timeout from spec setting").config(invocations = 3) {
         delay(10.hours)
      }

      // should apply to factories too
      include(factory)
   }
}
