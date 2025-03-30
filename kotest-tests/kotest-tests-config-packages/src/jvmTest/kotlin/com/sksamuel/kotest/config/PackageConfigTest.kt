package com.sksamuel.kotest.config

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

@EnabledIf(LinuxOnlyGithubCondition::class)
class PackageConfigTest : FunSpec() {
   init {
      test("package level config should be detected") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BarTest::class)
            .launch()
         // if the package config isn't picked up, this test won't timeout
         collector.result("bar")?.errorOrNull?.message shouldBe "Test 'bar' did not complete within 2ms"
      }
   }
}

private class BarTest : FunSpec({
   test("bar") {
      delay(10000000)
   }
})
