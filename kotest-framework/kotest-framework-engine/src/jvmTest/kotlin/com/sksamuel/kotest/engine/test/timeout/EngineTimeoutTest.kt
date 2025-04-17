package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class EngineTimeoutTest : FunSpec() {
   init {

      test("timeouts should be applied by the engine to suspend delays") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(DannyDelay::class)
            .launch()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "Test 'a' did not complete within 400ms"
         }
      }

      test("timeouts should be applied by the engine to suspend inside launched coroutines") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(LarryLauncher::class)
            .launch()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "Test 'a' did not complete within 400ms"
         }
      }

      test("timeouts should be applied by the engine to blocked threads") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BillyBlocked::class)
            .launch()
         collector.names shouldBe listOf("a")
         collector.result("a").asClue { result ->
            result?.errorOrNull?.message shouldBe "sleep interrupted"
         }
      }
   }
}

private class DannyDelay : FunSpec() {
   init {
      test("a").config(timeout = 400.milliseconds) {
         delay(24.hours)
      }
   }
}

private class LarryLauncher : FunSpec() {
   init {
      test("a").config(timeout = 400.milliseconds) {
         launch {
            delay(24.hours)
         }
      }
   }
}

private class BillyBlocked : FunSpec() {
   init {
      test("a").config(timeout = 400.milliseconds, blockingTest = true) {
         Thread.sleep(1_000_000)
      }
   }
}
