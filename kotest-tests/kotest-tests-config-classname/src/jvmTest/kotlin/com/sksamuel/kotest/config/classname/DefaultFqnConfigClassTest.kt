package com.sksamuel.kotest.config.classname

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@EnabledIf(LinuxCondition::class)
class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("default FQN should be checked for config class if no system property is set") {
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
