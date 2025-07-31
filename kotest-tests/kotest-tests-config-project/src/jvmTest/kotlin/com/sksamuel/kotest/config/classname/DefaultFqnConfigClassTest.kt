package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.Description
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@Description("Tests that the default FQN is used and if it wasn't applied the test would not timeout")
class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("default FQN should be used for config class when no sys property override exists") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher()
            .withListener(collector)
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
