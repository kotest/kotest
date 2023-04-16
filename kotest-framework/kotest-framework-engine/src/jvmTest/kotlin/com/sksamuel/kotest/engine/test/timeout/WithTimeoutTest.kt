package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
class WithTimeoutTest : FunSpec() {
   init {
      test("a users withTimeout should not be caught by InvocationTimeoutInterceptor") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(WithTimeoutSpec::class)
            .launch()
         collector.result("a")!!.errorOrNull!!.message shouldBe """Timed out waiting for 1000 ms"""

      }
   }
}

@ExperimentalTime
private class WithTimeoutSpec : FunSpec() {
   init {
      test("a") {
         withTimeout(1.seconds) {
            delay(1.minutes)
         }
      }
   }
}
