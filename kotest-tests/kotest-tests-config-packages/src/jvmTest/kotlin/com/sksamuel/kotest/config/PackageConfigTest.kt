package com.sksamuel.kotest.config

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.days

@EnabledIf(LinuxOnlyGithubCondition::class)
class PackageConfigTest : FunSpec() {
   init {
      test("package level config should be detected") {
         val collector = CollectingTestEngineListener()

         // todo I do not know why runblocking is required here, but without it, the collector results are empty
         runBlocking {
            TestEngineLauncher()
               .withListener(collector)
               .withSpecRefs(SpecRef.Reference(BarTest::class))
               .execute()
         }

         // if the package config isn't picked up, this test would not timeout
         collector.result("bar")?.errorOrNull?.message shouldBe "Test 'bar' did not complete within 22ms"
      }
   }
}

private class BarTest : FunSpec({
   test("bar") {
      delay(1.days)
   }
})
