package com.sksamuel.kotest.config

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@EnabledIf(LinuxOnlyGithubCondition::class)
class PackageConfigTest : FunSpec() {
   init {
      // The PackageConfig in this package sets invocationTimeout = 22ms, which applies to this test too.
      // We must override it here so the outer test has enough time for the inner engine to complete.
      test("package level config should be detected").config(invocationTimeout = 1.minutes) {
         val collector = CollectingTestEngineListener()

         TestEngineLauncher()
            .withListener(collector)
            .withSpecRefs(SpecRef.Reference(BarTest::class))
            .execute()

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
