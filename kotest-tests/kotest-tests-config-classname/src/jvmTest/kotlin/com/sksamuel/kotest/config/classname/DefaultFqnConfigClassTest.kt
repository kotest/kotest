package com.sksamuel.kotest.config.classname

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class DefaultFqnConfigClassTest : FunSpec() {
   init {
      test("default FQN should be checked for config class if no system property is set") {
         val projectConfiguration = ProjectConfiguration()
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withConfiguration(projectConfiguration)
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
