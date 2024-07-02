package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

class EngineTimeoutTest : FunSpec() {
   init {

      test("timeouts should be applied by the engine to suspend delays") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(DannyDelay::class)
            .launch()
         collector.waitForEnginesFinished()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "Test 'a' did not complete within 1ms"
         }
      }

      test("timeouts should be applied by the engine to suspend inside launched coroutines") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(LarryLauncher::class)
            .launch()
         collector.waitForEnginesFinished()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "Test 'a' did not complete within 1ms"
         }
      }

      test("timeouts should be applied by the engine to blocked threads") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BillyBlocked::class)
            .launch()
         collector.waitForEnginesFinished()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "sleep interrupted"
         }
      }
   }
}

private class DannyDelay : FunSpec() {
   init {
      test("a").config(timeout = 1.milliseconds) {
         delay(24.hours)
      }
   }
}

private class LarryLauncher : FunSpec() {
   init {
      test("a").config(timeout = 1.milliseconds) {
         launch {
            delay(24.hours)
         }
      }
   }
}

private class BillyBlocked : FunSpec() {
   init {
      test("a").config(timeout = 1.milliseconds, blockingTest = true) {
         Thread.sleep(1_000_000)
      }
   }
}
