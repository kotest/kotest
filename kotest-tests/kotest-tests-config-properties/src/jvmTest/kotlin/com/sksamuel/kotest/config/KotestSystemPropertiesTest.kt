package com.sksamuel.kotest.config

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@Description("Tests that the kotest.properties file is picked up and if it wasn't applied the test would not timeout")
class KotestSystemPropertiesTest : FunSpec() {
   init {
      test("FQN from kotest.properties should be used when defined") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BarTest::class)
            .launch()
         collector.result("bar")?.errorOrNull?.message shouldBe "Test 'bar' did not complete within 2ms"
      }
   }
}

private class BarTest : FunSpec({
   test("bar") {
      delay(10000000)
   }
})
