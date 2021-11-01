package com.sksamuel.kotest.engine.test.timeout

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration

@ExperimentalKotest
class ContainerTimeoutTest : FunSpec() {
   init {
      test("container test should timeout if nested exceeds parent timeout") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(NestedTimeout::class)
            .launch()
         collector.tests.keys.map { it.name.testName }.toSet() shouldBe setOf("a")
         collector.tests.values.map { it.errorOrNull?.message }.toSet() shouldBe setOf(
            "Test 'a' did not complete within 10ms",
         )
      }
   }
}

@ExperimentalKotest
private class NestedTimeout : FunSpec() {
   init {
      context("a").config(timeout = Duration.milliseconds(10)) {
         test("b") {
            delay(Duration.milliseconds(200))
         }
      }
   }
}
